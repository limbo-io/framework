package io.limbo.cqrs.core.query;

/**
 * Functional interface for handling queries.
 *
 * @param <Q> the query type
 * @param <R> the result type
 */
@FunctionalInterface
public interface QueryHandler<Q, R> {

    /**
     * Handles the given query message.
     *
     * @param query the query message
     * @return the query result
     * @throws Exception if handling fails
     */
    R handle(QueryMessage<Q> query) throws Exception;
}
