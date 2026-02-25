package io.limbo.cqrs.core.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Simple facade for sending queries.
 * Provides convenient methods for query dispatch with various options.
 */
public interface QueryGateway {

    /**
     * Sends a query and waits for the result.
     *
     * @param query      the query to send
     * @param resultType the expected result type
     * @param <R>        the result type
     * @return the query result
     */
    <R> R query(Query query, Class<R> resultType);

    /**
     * Sends a query and waits for the result with timeout.
     *
     * @param query      the query to send
     * @param resultType the expected result type
     * @param timeout    the timeout value
     * @param unit       the timeout unit
     * @param <R>        the result type
     * @return the query result
     * @throws RuntimeException if timeout or execution fails
     */
    <R> R query(Query query, Class<R> resultType, long timeout, TimeUnit unit);

    /**
     * Sends a query asynchronously.
     *
     * @param query      the query to send
     * @param resultType the expected result type
     * @param <R>        the result type
     * @return future containing the query result
     */
    <R> CompletableFuture<R> queryAsync(Query query, Class<R> resultType);

    /**
     * Sends a query to multiple handlers and gathers results.
     * Useful for scatter-gather pattern.
     *
     * @param query      the query to send
     * @param resultType the expected result type
     * @param timeout    the timeout value
     * @param unit       the timeout unit
     * @param <R>        the result type
     * @return list of results from all handlers
     */
    <R> List<R> scatterGather(Query query, Class<R> resultType, long timeout, TimeUnit unit);
}
