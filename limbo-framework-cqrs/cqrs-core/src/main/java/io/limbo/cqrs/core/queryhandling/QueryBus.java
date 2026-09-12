package io.limbo.cqrs.core.queryhandling;

import io.limbo.cqrs.core.message.QueryMessage;
import io.limbo.cqrs.core.message.QueryResultMessage;

import java.util.concurrent.CompletableFuture;

/**
 * Bus for dispatching queries to their handlers.
 */
public interface QueryBus {

    /**
     * Dispatch a query synchronously.
     *
     * @param query the query
     * @param <Q>   the query type
     * @param <R>   the result type
     * @return the result
     * @throws io.limbo.cqrs.core.commandhandling.HandlerNotFoundException if no handler is found
     */
    <Q extends IQuery<R>, R> R dispatch(Q query);

    /**
     * Dispatch a query asynchronously.
     *
     * @param query the query
     * @param <Q>   the query type
     * @param <R>   the result type
     * @return a CompletableFuture with the result
     */
    <Q extends IQuery<R>, R> CompletableFuture<R> dispatchAsync(Q query);

    /**
     * Dispatch a query message enveloped.
     *
     * @param message the query message
     * @param <Q>     the query type
     * @param <R>     the result type
     * @return the result message
     */
    <Q extends IQuery<R>, R> QueryResultMessage<R> dispatch(QueryMessage<Q, R> message);

    /**
     * Register a query handler.
     *
     * @param queryType the query type
     * @param handler   the handler
     * @param <Q>       the query type
     * @param <R>       the result type
     * @return a registration that can be used to unregister
     */
    <Q extends IQuery<R>, R> HandlerRegistration register(Class<Q> queryType, QueryHandler<Q, R> handler);

    /**
     * shutdown the query bus.
     */
    void shutdown();
}
