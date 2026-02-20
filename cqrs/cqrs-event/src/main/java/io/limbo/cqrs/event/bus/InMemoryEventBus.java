package io.limbo.cqrs.event.bus;

import io.limbo.cqrs.event.EventBus;
import io.limbo.cqrs.event.EventMessage;
import io.limbo.cqrs.event.handler.EventHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * In-memory implementation of event bus using pub-sub pattern.
 * Thread-safe for concurrent subscription and publishing.
 */
public class InMemoryEventBus implements EventBus {

    private final Map<Class<?>, Set<EventHandler<?>>> subscriptions = new ConcurrentHashMap<>();

    @Override
    public void publish(EventMessage<?> event) {
        Class<?> payloadType = event.getPayloadType();
        Set<EventHandler<?>> handlers = subscriptions.get(payloadType);

        if (handlers == null || handlers.isEmpty()) {
            return;
        }

        for (EventHandler<?> handler : handlers) {
            try {
                invokeHandler(handler, event);
            } catch (Exception e) {
                throw new EventPublicationException(
                        "Failed to publish event of type: " + payloadType.getName(), e);
            }
        }
    }

    @Override
    public void publishAll(List<EventMessage<?>> events) {
        if (events == null) {
            return;
        }
        for (EventMessage<?> event : events) {
            publish(event);
        }
    }

    @Override
    public <T> Runnable subscribe(Class<T> eventType, EventHandler<T> handler) {
        subscriptions.computeIfAbsent(eventType, k -> new CopyOnWriteArraySet<>()).add(handler);

        return () -> {
            Set<EventHandler<?>> handlers = subscriptions.get(eventType);
            if (handlers != null) {
                handlers.remove(handler);
                if (handlers.isEmpty()) {
                    subscriptions.remove(eventType);
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    private <T> void invokeHandler(EventHandler<T> handler, EventMessage<?> event) throws Exception {
        handler.handle((EventMessage<T>) event);
    }

    /**
     * Clears all subscriptions. Useful for testing.
     */
    public void clearSubscriptions() {
        subscriptions.clear();
    }

    /**
     * Exception thrown when event publication fails.
     */
    public static class EventPublicationException extends RuntimeException {
        public EventPublicationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
