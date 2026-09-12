package io.limbo.cqrs.spring.gateway;

import io.limbo.cqrs.core.commandhandling.CommandBus;
import io.limbo.cqrs.core.commandhandling.ICommand;

import java.util.concurrent.CompletableFuture;

/**
 * Gateway for dispatching commands.
 * This is the recommended way to send commands from application code.
 */
public class CommandGateway {

    private final CommandBus commandBus;

    public CommandGateway(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    /**
     * Send a command synchronously.
     *
     * @param command the command to send
     * @param <C>     the command type
     * @param <R>     the result type
     * @return the result
     */
    public <C extends ICommand, R> R send(C command) {
        return commandBus.dispatch(command);
    }

    /**
     * Send a command asynchronously.
     *
     * @param command the command to send
     * @param <C>     the command type
     * @param <R>     the result type
     * @return a CompletableFuture with the result
     */
    public <C extends ICommand, R> CompletableFuture<R> sendAsync(C command) {
        return commandBus.dispatchAsync(command);
    }
}
