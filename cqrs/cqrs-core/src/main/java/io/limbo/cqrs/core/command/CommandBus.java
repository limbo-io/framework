package io.limbo.cqrs.core.command;

/**
 * Interface for dispatching commands to handlers.
 */
public interface CommandBus {

    /**
     * Executes a command synchronously.
     *
     * @param command the command to execute
     * @return the handler result
     * @throws io.limbo.cqrs.core.handler.HandlerNotFoundException if no handler registered
     */
    Object execute(Command command);

    /**
     * Registers a handler for the given command type.
     *
     * @param commandType the command type
     * @param handler     the handler to register
     * @param <T>         the command type
     */
    <T extends Command> void register(Class<T> commandType, CommandHandler<T> handler);
}
