package io.limbo.cqrs.event.store;

import io.limbo.cqrs.event.EventMessage;

import java.util.List;

/**
 * Interface for persisting and retrieving events.
 */
public interface EventStore {

    /**
     * Appends a single event for the given aggregate.
     *
     * @param aggregateId the aggregate identifier
     * @param event       the event to append
     */
    void appendEvent(String aggregateId, EventMessage<?> event);

    /**
     * Appends multiple events for the given aggregate.
     *
     * @param aggregateId the aggregate identifier
     * @param events      the events to append
     */
    void appendEvents(String aggregateId, List<EventMessage<?>> events);

    /**
     * Gets all events for the given aggregate.
     *
     * @param aggregateId the aggregate identifier
     * @return list of events, empty list if aggregate not found
     */
    List<EventMessage<?>> getEvents(String aggregateId);
}
