package io.limbo.cqrs.core.commandhandling;

/**
 * Handler for commands.
 *
 * @param <C> the command type
 * @param <R> the result type
 */
@FunctionalInterface
public interface CommandHandler<C extends ICommand, R> {

    /**
     * Handle the command and return a result.
     *
     * @param command the command to handle
     * @return the result
     */
    R handle(C command);

    /**
     * Check if this handler can handle the given command type.
     *
     * @param commandType the command type
     * @return true if supported
     */
    default boolean canHandle(Class<? extends ICommand> commandType) {
        return true;
    }
}
