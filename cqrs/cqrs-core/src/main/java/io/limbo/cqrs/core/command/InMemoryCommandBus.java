package io.limbo.cqrs.core.command;

import io.limbo.cqrs.core.handler.HandlerNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of command bus.
 * Thread-safe for concurrent registration and execution.
 */
public class InMemoryCommandBus implements CommandBus {

    private final Map<Class<? extends Command>, CommandHandler<?>> handlers = new ConcurrentHashMap<>();

    @Override
    public Object execute(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        CommandHandler<Command> handler = findHandler(command.getClass());
        return handler.handle(command);
    }

    @Override
    public <T extends Command> void register(Class<T> commandType, CommandHandler<T> handler) {
        if (commandType == null) {
            throw new IllegalArgumentException("Command type cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        handlers.put(commandType, handler);
    }

    @SuppressWarnings("unchecked")
    private <T extends Command> CommandHandler<T> findHandler(Class<?> commandType) {
        CommandHandler<?> handler = handlers.get(commandType);
        if (handler == null) {
            throw new HandlerNotFoundException(commandType);
        }
        return (CommandHandler<T>) handler;
    }

    /**
     * Clears all registrations. Useful for testing.
     */
    public void clear() {
        handlers.clear();
    }
}
