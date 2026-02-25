package io.limbo.cqrs.core.command;

import io.limbo.cqrs.core.handler.HandlerNotFoundException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of command bus.
 * Thread-safe for concurrent registration and dispatch.
 */
public class InMemoryCommandBus implements CommandBus {

    private final Map<Class<?>, CommandHandler<?>> handlers = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public Object dispatch(CommandMessage<?> command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        CommandHandler handler = findHandler(command.getPayloadType());

        try {
            return handler.handle((CommandMessage) command);
        } catch (Exception e) {
            throw new CommandExecutionException(
                    "Failed to execute command: " + command.getPayloadType().getName(), e);
        }
    }

    @Override
    public CompletableFuture<Object> dispatchAsync(CommandMessage<?> command) {
        return CompletableFuture.supplyAsync(() -> dispatch(command));
    }

    @Override
    public <T> void register(Class<T> commandType, CommandHandler<T> handler) {
        if (commandType == null) {
            throw new IllegalArgumentException("Command type cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        handlers.put(commandType, handler);
    }

    private CommandHandler<?> findHandler(Class<?> commandType) {
        CommandHandler<?> handler = handlers.get(commandType);
        if (handler == null) {
            throw new HandlerNotFoundException(commandType);
        }
        return handler;
    }

    /**
     * Clears all registrations. Useful for testing.
     */
    public void clearRegistrations() {
        handlers.clear();
    }

    /**
     * Exception thrown when command execution fails.
     */
    public static class CommandExecutionException extends RuntimeException {
        public CommandExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
