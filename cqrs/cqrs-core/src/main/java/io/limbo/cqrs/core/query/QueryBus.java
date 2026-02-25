package io.limbo.cqrs.core.query;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for dispatching queries to handlers.
 */
public interface QueryBus {

    /**
     * Dispatches a query synchronously.
     *
     * @param query the query to dispatch
     * @return the handler result
     * @throws io.limbo.cqrs.core.handler.HandlerNotFoundException if no handler registered
     */
    Object query(QueryMessage<?> query);

    /**
     * Dispatches a query and returns result of specific type.
     *
     * @param query      the query to dispatch
     * @param resultType the expected result type
     * @param <R>        the result type
     * @return the handler result
     */
    <R> R query(QueryMessage<?> query, Class<R> resultType);

    /**
     * Dispatches a query asynchronously.
     *
     * @param query the query to dispatch
     * @return future containing the handler result
     */
    CompletableFuture<Object> queryAsync(QueryMessage<?> query);

    /**
     * Registers a handler for the given query type.
     *
     * @param queryType    the query type
     * @param resultType   the result type
     * @param handler      the handler to register
     * @param <Q>          the query type
     * @param <R>          the result type
     */
    <Q, R> void register(Class<Q> queryType, Class<R> resultType, QueryHandler<Q, R> handler);
}
