package io.limbo.cqrs.core.query;

import io.limbo.cqrs.core.handler.HandlerNotFoundException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of query bus.
 * Thread-safe for concurrent registration and dispatch.
 */
public class InMemoryQueryBus implements QueryBus {

    private final Map<QueryKey<?, ?>, QueryHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public Object query(QueryMessage<?> query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        QueryHandler handler = findHandler(query.getPayloadType());

        try {
            return handler.handle(query);
        } catch (Exception e) {
            throw new QueryExecutionException(
                    "Failed to execute query: " + query.getPayloadType().getName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(QueryMessage<?> query, Class<R> resultType) {
        Object result = query(query);
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
    public CompletableFuture<Object> queryAsync(QueryMessage<?> query) {
        return CompletableFuture.supplyAsync(() -> query(query));
    }

    @Override
    public <Q, R> void register(Class<Q> queryType, Class<R> resultType, QueryHandler<Q, R> handler) {
        if (queryType == null) {
            throw new IllegalArgumentException("Query type cannot be null");
        }
        if (resultType == null) {
            throw new IllegalArgumentException("Result type cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        handlers.put(new QueryKey<>(queryType, resultType), handler);
    }

    private QueryHandler<?, ?> findHandler(Class<?> queryType) {
        // Find handler by query type only (ignoring result type for lookup)
        return handlers.entrySet().stream()
                .filter(e -> e.getKey().queryType.equals(queryType))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new HandlerNotFoundException(queryType));
    }

    /**
     * Clears all registrations. Useful for testing.
     */
    public void clearRegistrations() {
        handlers.clear();
    }

    /**
     * Key for indexing query handlers by query type and result type.
     */
    private static class QueryKey<Q, R> {
        final Class<Q> queryType;
        final Class<R> resultType;

        QueryKey(Class<Q> queryType, Class<R> resultType) {
            this.queryType = Objects.requireNonNull(queryType);
            this.resultType = Objects.requireNonNull(resultType);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QueryKey<?, ?> queryKey = (QueryKey<?, ?>) o;
            return Objects.equals(queryType, queryKey.queryType) &&
                    Objects.equals(resultType, queryKey.resultType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(queryType, resultType);
        }
    }

    /**
     * Exception thrown when query execution fails.
     */
    public static class QueryExecutionException extends RuntimeException {
        public QueryExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
