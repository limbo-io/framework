package io.limbo.cqrs.core.command;

import io.limbo.cqrs.core.handler.HandlerNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of command bus.
 * Thread-safe for concurrent registration and execution.
 */
public class InMemoryCommandBus implements CommandBus {

    private final Map<Class<? extends ICommand>, CommandHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    @Override
    public Object execute(ICommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        CommandHandler<ICommand, Object> handler = findHandler(command.getClass());
        return handler.handle(command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R execute(ICommand command, Class<R> resultType) {
        Object result = execute(command);
        if (result == null) {
            return null;
        }
        if (!resultType.isInstance(result)) {
            throw new ClassCastException(
                    "Command result type mismatch. Expected: " + resultType.getName() +
                            ", Actual: " + result.getClass().getName());
        }
        return (R) result;
    }

    @Override
    public <T extends ICommand, R> void register(Class<T> commandType, CommandHandler<T, R> handler) {
        if (commandType == null) {
            throw new IllegalArgumentException("Command type cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        handlers.put(commandType, handler);
    }

    @SuppressWarnings("unchecked")
    private <T extends ICommand, R> CommandHandler<T, R> findHandler(Class<?> commandType) {
        CommandHandler<?, ?> handler = handlers.get(commandType);
        if (handler == null) {
            throw new HandlerNotFoundException(commandType);
        }
        return (CommandHandler<T, R>) handler;
    }

    /**
     * Clears all registrations. Useful for testing.
     */
    public void clear() {
        handlers.clear();
    }
}
