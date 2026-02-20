package io.limbo.cqrs.core.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for mapping message types to their handlers.
 * Thread-safe for concurrent registration and lookup.
 *
 * @param <M> base message type
 * @param <R> base result type
 */
public class HandlerRegistry<M, R> {

    private final Map<Class<? extends M>, Handler<? extends M, ? extends R>> handlers = new ConcurrentHashMap<>();

    /**
     * Registers a handler for the given message type.
     * Overrides any existing registration.
     *
     * @param messageType the message type to handle
     * @param handler     the handler to register
     * @param <T>         specific message type
     */
    public <T extends M> void register(Class<T> messageType, Handler<T, R> handler) {
        handlers.put(messageType, handler);
    }

    /**
     * Finds the handler registered for the given message type.
     *
     * @param messageType the message type to find handler for
     * @param <T>         specific message type
     * @return the registered handler
     * @throws HandlerNotFoundException if no handler is registered
     */
    @SuppressWarnings("unchecked")
    public <T extends M> Handler<T, R> findHandler(Class<T> messageType) {
        Handler<T, R> handler = (Handler<T, R>) handlers.get(messageType);
        if (handler == null) {
            throw new HandlerNotFoundException(messageType);
        }
        return handler;
    }

    /**
     * Checks if a handler exists for the given message type.
     *
     * @param messageType the message type to check
     * @return true if handler exists
     */
    public boolean hasHandler(Class<? extends M> messageType) {
        return handlers.containsKey(messageType);
    }

    /**
     * Removes all registrations.
     */
    public void clear() {
        handlers.clear();
    }
}
