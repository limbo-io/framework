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
    Object execute(ICommand command);

    /**
     * Executes a command and returns result of specific type.
     *
     * @param command    the command to execute
     * @param resultType the expected result type
     * @param <R>        the result type
     * @return the handler result
     * @throws io.limbo.cqrs.core.handler.HandlerNotFoundException if no handler registered
     */
    <R> R execute(ICommand command, Class<R> resultType);

    /**
     * Registers a handler for the given command type.
     *
     * @param commandType the command type
     * @param handler     the handler to register
     * @param <T>         the command type
     * @param <R>         the result type
     */
    <T extends ICommand, R> void register(Class<T> commandType, CommandHandler<T, R> handler);
}
