package io.limbo.cqrs.spring.query;

import io.limbo.cqrs.core.query.IQuery;
import io.limbo.cqrs.core.query.QueryBus;
import io.limbo.utils.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Static query executor that integrates with Spring ApplicationContext.
 * <p>
 * Usage:
 * <pre>
 * UserId userId = Query.query(new GetUserByEmailQuery("test@example.com"));
 * </pre>
 * <p>
 * The return type is automatically inferred from the query's generic parameter.
 *
 * @author Limbo Framework Team
 */
public class Query implements ApplicationContextAware {

    private static QueryBus BUS;

    /**
     * Executes a query and returns the result with automatic type inference.
     *
     * @param query the query to execute (must implement IQuery<R>)
     * @param <R>   the expected return type (inferred from query's generic parameter)
     * @param <Q>   the query type
     * @return the query result
     * @throws IllegalArgumentException  if query is null or generic type cannot be resolved
     * @throws IllegalStateException     if QueryBus is not initialized
     * @throws io.limbo.cqrs.core.handler.HandlerNotFoundException if no handler registered for the query
     */
    public static <R, Q extends IQuery<R>> R query(Q query) {
        if (BUS == null) {
            throw new IllegalStateException(
                    "QueryBus is not initialized. Ensure Query bean is registered and Spring context is initialized.");
        }

        Class<R> responseType = ReflectionUtils.refType(query);
        return BUS.execute(query, responseType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BUS = applicationContext.getBean(QueryBus.class);
    }
}
