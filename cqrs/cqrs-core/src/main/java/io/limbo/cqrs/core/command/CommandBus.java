package io.limbo.cqrs.core.command;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for dispatching commands to handlers.
 */
public interface CommandBus {

    /**
     * Dispatches a command synchronously.
     *
     * @param command the command to dispatch
     * @return the handler result
     * @throws io.limbo.cqrs.core.handler.HandlerNotFoundException if no handler registered
     */
    Object dispatch(CommandMessage<?> command);

    /**
     * Dispatches a command asynchronously.
     *
     * @param command the command to dispatch
     * @return future containing the handler result
     */
    CompletableFuture<Object> dispatchAsync(CommandMessage<?> command);

    /**
     * Registers a handler for the given command type.
     *
     * @param commandType the command type
     * @param handler     the handler to register
     * @param <T>         the command type
     */
    <T> void register(Class<T> commandType, CommandHandler<T> handler);
}
