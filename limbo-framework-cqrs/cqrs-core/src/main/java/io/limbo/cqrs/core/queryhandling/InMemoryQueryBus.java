package io.limbo.cqrs.core.queryhandling;

import io.limbo.cqrs.core.commandhandling.HandlerNotFoundException;
import io.limbo.cqrs.core.message.QueryMessage;
import io.limbo.cqrs.core.message.QueryResultMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * In-memory implementation of QueryBus.
 */
public class InMemoryQueryBus implements QueryBus {

    private final ConcurrentHashMap<Class<? extends IQuery<?>>, QueryHandler<?, ?>> handlers = new ConcurrentHashMap<>();
    private final ExecutorService asyncExecutor;

    public InMemoryQueryBus() {
        this.asyncExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public InMemoryQueryBus(ExecutorService asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Q extends IQuery<R>, R> R dispatch(Q query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        QueryHandler<Q, R> handler = (QueryHandler<Q, R>) handlers.get(query.getClass());
        if (handler == null) {
            throw new HandlerNotFoundException("No handler found for query: " + query.getClass().getName());
        }

        return handler.handle(query);
    }

    @Override
    public <Q extends IQuery<R>, R> CompletableFuture<R> dispatchAsync(Q query) {
        return CompletableFuture.supplyAsync(() -> dispatch(query), asyncExecutor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Q extends IQuery<R>, R> QueryResultMessage<R> dispatch(QueryMessage<Q, R> message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        try {
            Q payload = message.getPayload();
            QueryHandler<Q, R> handler = (QueryHandler<Q, R>) handlers.get(payload.getClass());
            if (handler == null) {
                throw new HandlerNotFoundException("No handler found for query: " + payload.getClass().getName());
            }

            R result = handler.handle(payload);
            return new QueryResultMessage<>(message.getIdentifier(), result, null);
        } catch (Exception e) {
            return new QueryResultMessage<>(message.getIdentifier(), null, e);
        }
    }

    @Override
    public <Q extends IQuery<R>, R> HandlerRegistration register(Class<Q> queryType, QueryHandler<Q, R> handler) {
        handlers.put(queryType, handler);
        return () -> handlers.remove(queryType, handler);
    }

    @Override
    public void shutdown() {
        asyncExecutor.shutdown();
    }
}
