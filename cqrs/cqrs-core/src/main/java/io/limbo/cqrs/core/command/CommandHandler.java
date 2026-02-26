package io.limbo.cqrs.core.command;

/**
 * Functional interface for handling commands.
 *
 * @param <T> the command type
 */
@FunctionalInterface
public interface CommandHandler<T extends Command> {

    /**
     * Handles the given command.
     *
     * @param command the command to handle
     * @return the handler result (can be null)
     */
    Object handle(T command);
}
