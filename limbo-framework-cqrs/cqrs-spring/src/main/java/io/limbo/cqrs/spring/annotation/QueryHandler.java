package io.limbo.cqrs.spring.annotation;

import java.lang.annotation.*;

/**
 * Marks a method as a query handler.
 * The method must accept a single parameter that implements {@link io.limbo.cqrs.core.queryhandling.IQuery}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryHandler {
}
