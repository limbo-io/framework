package io.limbo.cqrs.spring.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enable CQRS support in Spring applications.
 * This annotation imports the necessary configuration for command and query handling.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CqrsSpringConfiguration.class)
public @interface EnableCqrs {
}
