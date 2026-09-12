package io.limbo.cqrs.core.queryhandling;

/**
 * Handler for queries.
 *
 * @param <Q> the query type
 * @param <R> the result type
 */
@FunctionalInterface
public interface QueryHandler<Q extends IQuery<R>, R> {

    /**
     * Handle the query and return a result.
     *
     * @param query the query to handle
     * @return the result
     */
    R handle(Q query);

    /**
     * Check if this handler can handle the given query type.
     *
     * @param queryType the query type
     * @return true if supported
     */
    default boolean canHandle(Class<? extends IQuery<?>> queryType) {
        return true;
    }
}
