package io.limbo.cqrs.event;

import io.limbo.cqrs.event.handler.EventHandler;

import java.util.List;

/**
 * Interface for publishing events to subscribers.
 */
public interface EventBus {

    /**
     * Publishes a single event.
     *
     * @param event the event to publish
     */
    void publish(EventMessage<?> event);

    /**
     * Publishes multiple events.
     *
     * @param events the events to publish
     */
    void publishAll(List<EventMessage<?>> events);

    /**
     * Subscribes a handler to events of the given type.
     *
     * @param eventType the event type to subscribe to
     * @param handler   the handler to invoke
     * @param <T>       the event type
     * @return a callback to unsubscribe
     */
    <T> Runnable subscribe(Class<T> eventType, EventHandler<T> handler);
}
