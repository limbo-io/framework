package io.limbo.cqrs.core.command;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link CommandGateway}.
 * Delegates to a {@link CommandBus} for actual dispatch.
 */
public class DefaultCommandGateway implements CommandGateway {

    private final CommandBus commandBus;

    /**
     * Creates gateway with the given command bus.
     *
     * @param commandBus the command bus to delegate to
     */
    public DefaultCommandGateway(CommandBus commandBus) {
        if (commandBus == null) {
            throw new IllegalArgumentException("CommandBus cannot be null");
        }
        this.commandBus = commandBus;
    }

    @Override
    public void send(Command command) {
        dispatch(command);
    }

    @Override
    public Object sendAndWait(Command command) {
        return dispatch(command);
    }

    @Override
    public Object sendAndWait(Command command, long timeout, TimeUnit unit) {
        try {
            return sendAsync(command).get(timeout, unit);
        } catch (Exception e) {
            throw new CommandDispatchException(
                    "Failed to dispatch command within timeout: " + command.getClass().getName(), e);
        }
    }

    @Override
    public CompletableFuture<Object> sendAsync(Command command) {
        return commandBus.dispatchAsync(toCommandMessage(command));
    }

    private Object dispatch(Command command) {
        return commandBus.dispatch(toCommandMessage(command));
    }

    private CommandMessage<?> toCommandMessage(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        return CommandMessage.asCommandMessage(command);
    }

    /**
     * Exception thrown when command dispatch fails.
     */
    public static class CommandDispatchException extends RuntimeException {
        public CommandDispatchException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
