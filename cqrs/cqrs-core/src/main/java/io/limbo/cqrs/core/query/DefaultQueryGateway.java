package io.limbo.cqrs.core.query;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link QueryGateway}.
 * Delegates to a {@link QueryBus} for actual dispatch.
 */
public class DefaultQueryGateway implements QueryGateway {

    private final QueryBus queryBus;

    /**
     * Creates gateway with the given query bus.
     *
     * @param queryBus the query bus to delegate to
     */
    public DefaultQueryGateway(QueryBus queryBus) {
        if (queryBus == null) {
            throw new IllegalArgumentException("QueryBus cannot be null");
        }
        this.queryBus = queryBus;
    }

    @Override
    public <R> R query(Query query, Class<R> resultType) {
        return queryBus.query(toQueryMessage(query), resultType);
    }

    @Override
    public <R> R query(Query query, Class<R> resultType, long timeout, TimeUnit unit) {
        try {
            return queryAsync(query, resultType).get(timeout, unit);
        } catch (Exception e) {
            throw new QueryDispatchException(
                    "Failed to dispatch query within timeout: " + query.getClass().getName(), e);
        }
    }

    @Override
    public <R> CompletableFuture<R> queryAsync(Query query, Class<R> resultType) {
        return queryBus.queryAsync(toQueryMessage(query))
                .thenApply(result -> {
                    if (result == null) {
                        return null;
                    }
                    if (!resultType.isInstance(result)) {
                        throw new ClassCastException(
                                "Query result type mismatch. Expected: " + resultType.getName() +
                                        ", Actual: " + result.getClass().getName());
                    }
                    return resultType.cast(result);
                });
    }

    @Override
    public <R> List<R> scatterGather(Query query, Class<R> resultType, long timeout, TimeUnit unit) {
        // Scatter-gather is not implemented in this lightweight version
        // This would require multiple handlers for the same query type
        // For now, return empty list
        return Collections.emptyList();
    }

    private QueryMessage<?> toQueryMessage(Query query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }
        return QueryMessage.asQueryMessage(query);
    }

    /**
     * Exception thrown when query dispatch fails.
     */
    public static class QueryDispatchException extends RuntimeException {
        public QueryDispatchException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
