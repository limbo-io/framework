package io.limbo.cqrs.spring.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables CQRS configuration in a Spring application.
 * Add this annotation to a configuration class to enable automatic configuration
 * of command, query, and event buses, as well as automatic handler registration.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CqrsSpringConfiguration.class)
public @interface EnableCqrs {

    /**
     * Base packages to scan for handlers.
     * If empty, the package of the annotated class is used.
     */
    String[] basePackages() default {};
}
