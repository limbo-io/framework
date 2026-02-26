package io.limbo.cqrs.core.command;

/**
 * Functional interface for handling commands.
 *
 * @param <T> the command type
 * @param <R> the result type
 */
@FunctionalInterface
public interface CommandHandler<T extends ICommand, R> {

    /**
     * Handles the given command.
     *
     * @param command the command to handle
     * @return the handler result (can be null)
     */
    R handle(T command);
}
