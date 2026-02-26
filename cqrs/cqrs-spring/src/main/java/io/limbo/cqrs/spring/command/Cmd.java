package io.limbo.cqrs.spring.command;

import io.limbo.cqrs.core.command.CommandBus;
import io.limbo.cqrs.core.command.ICommand;
import io.limbo.utils.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Static command executor that integrates with Spring ApplicationContext.
 * <p>
 * Usage:
 * <pre>
 * UserId userId = Command.execute(new CreateUserCommand("test@example.com"));
 * </pre>
 * <p>
 * The return type is automatically inferred from the command's generic parameter.
 *
 * @author Limbo Framework Team
 */
public class Cmd implements ApplicationContextAware {

    private static CommandBus BUS;

    /**
     * Executes a command and returns the result with automatic type inference.
     *
     * @param command the command to execute (must implement ICommand<R>)
     * @param <R>     the expected return type (inferred from command's generic parameter)
     * @param <C>     the command type
     * @return the command result
     * @throws IllegalArgumentException  if command is null or generic type cannot be resolved
     * @throws IllegalStateException     if CommandBus is not initialized
     * @throws io.limbo.cqrs.core.handler.HandlerNotFoundException if no handler registered for the command
     */
    public static <R, C extends ICommand<R>> R send(C command) {
        if (BUS == null) {
            throw new IllegalStateException(
                    "CommandBus is not initialized. Ensure Command bean is registered and Spring context is initialized.");
        }

        Class<R> responseType = ReflectionUtils.refType(command);
        return BUS.execute(command, responseType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BUS = applicationContext.getBean(CommandBus.class);
    }
}
