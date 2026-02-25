package io.limbo.cqrs.core.command;

/**
 * Functional interface for handling commands.
 *
 * @param <T> the command payload type
 */
@FunctionalInterface
public interface CommandHandler<T> {

    /**
     * Handles the given command message.
     *
     * @param command the command message
     * @return the handler result (can be null)
     * @throws Exception if handling fails
     */
    Object handle(CommandMessage<T> command) throws Exception;
}
