package io.limbo.cqrs.core.query;

import io.limbo.cqrs.core.handler.HandlerNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of query bus.
 * Thread-safe for concurrent registration and execution.
 */
public class InMemoryQueryBus implements QueryBus {

    private final Map<Class<? extends IQuery>, QueryHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public Object execute(IQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        QueryHandler<IQuery, Object> handler = findHandler(query.getClass());
        return handler.handle(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R execute(IQuery query, Class<R> resultType) {
        Object result = execute(query);
        if (result == null) {
            return null;
        }
        if (!resultType.isInstance(result)) {
            throw new ClassCastException(
                    "Query result type mismatch. Expected: " + resultType.getName() +
                            ", Actual: " + result.getClass().getName());
        }
        return (R) result;
    }

    @Override
    public <Q extends IQuery, R> void register(Class<Q> queryType, QueryHandler<Q, R> handler) {
        if (queryType == null) {
            throw new IllegalArgumentException("Query type cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        handlers.put(queryType, handler);
    }

    @SuppressWarnings("unchecked")
    private <Q extends IQuery, R> QueryHandler<Q, R> findHandler(Class<?> queryType) {
        QueryHandler<?, ?> handler = handlers.get(queryType);
        if (handler == null) {
            throw new HandlerNotFoundException(queryType);
        }
        return (QueryHandler<Q, R>) handler;
    }

    /**
     * Clears all registrations. Useful for testing.
     */
    public void clear() {
        handlers.clear();
    }
}
