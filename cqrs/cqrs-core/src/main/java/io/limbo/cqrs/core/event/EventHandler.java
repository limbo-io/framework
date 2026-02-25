package io.limbo.cqrs.core.event;

/**
 * Functional interface for handling events.
 *
 * @param <T> the event payload type
 */
@FunctionalInterface
public interface EventHandler<T> {

    /**
     * Handles the given event message.
     *
     * @param event the event message
     * @throws Exception if handling fails
     */
    void handle(EventMessage<T> event) throws Exception;
}
