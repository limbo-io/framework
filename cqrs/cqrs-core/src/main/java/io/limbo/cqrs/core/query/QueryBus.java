package io.limbo.cqrs.core.query;

/**
 * Interface for executing queries through handlers.
 */
public interface QueryBus {

    /**
     * Executes a query synchronously.
     *
     * @param query the query to execute
     * @return the handler result
     * @throws io.limbo.cqrs.core.handler.HandlerNotFoundException if no handler registered
     */
    Object execute(IQuery query);

    /**
     * Executes a query and returns result of specific type.
     *
     * @param query      the query to execute
     * @param resultType the expected result type
     * @param <R>        the result type
     * @return the handler result
     */
    <R> R execute(IQuery query, Class<R> resultType);

    /**
     * Registers a handler for the given query type.
     *
     * @param queryType    the query type
     * @param handler      the handler to register
     * @param <Q>          the query type
     * @param <R>          the result type
     */
    <Q extends IQuery, R> void register(Class<Q> queryType, QueryHandler<Q, R> handler);
}
