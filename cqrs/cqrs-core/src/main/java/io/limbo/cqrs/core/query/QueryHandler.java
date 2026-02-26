package io.limbo.cqrs.core.query;

/**
 * Functional interface for handling queries.
 *
 * @param <Q> the query type
 * @param <R> the result type
 */
@FunctionalInterface
public interface QueryHandler<Q extends IQuery, R> {

    /**
     * Handles the given query.
     *
     * @param query the query to handle
     * @return the query result
     */
    R handle(Q query);
}
