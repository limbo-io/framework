package io.limbo.cqrs.spring.annotation;

import java.lang.annotation.*;

/**
 * Marks a method as a command handler.
 * The method must accept a single parameter that implements {@link io.limbo.cqrs.core.commandhandling.ICommand}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandHandler {
}
