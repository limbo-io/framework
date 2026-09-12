package io.limbo.cqrs.spring.gateway;

import io.limbo.cqrs.core.queryhandling.IQuery;
import io.limbo.cqrs.core.queryhandling.QueryBus;

import java.util.concurrent.CompletableFuture;

/**
 * Gateway for dispatching queries.
 * This is the recommended way to send queries from application code.
 */
public class QueryGateway {

    private final QueryBus queryBus;

    public QueryGateway(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    /**
     * Send a query synchronously.
     *
     * @param query the query to send
     * @param <Q>   the query type
     * @param <R>   the result type
     * @return the result
     */
    public <Q extends IQuery<R>, R> R query(Q query) {
        return queryBus.dispatch(query);
    }

    /**
     * Send a query asynchronously.
     *
     * @param query the query to send
     * @param <Q>   the query type
     * @param <R>   the result type
     * @return a CompletableFuture with the result
     */
    public <Q extends IQuery<R>, R> CompletableFuture<R> queryAsync(Q query) {
        return queryBus.dispatchAsync(query);
    }
}
