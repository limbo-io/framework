package io.limbo.cqrs.core.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of event store.
 * Thread-safe for concurrent reads and writes.
 */
public class InMemoryEventStore implements EventStore {

    private final Map<String, List<EventMessage<?>>> eventsByAggregate = new ConcurrentHashMap<>();

    @Override
    public void appendEvent(String aggregateId, EventMessage<?> event) {
        List<EventMessage<?>> events = new ArrayList<>();
        events.add(event);
        appendEvents(aggregateId, events);
    }

    @Override
    public void appendEvents(String aggregateId, List<EventMessage<?>> events) {
        if (aggregateId == null) {
            throw new IllegalArgumentException("Aggregate ID cannot be null");
        }
        if (events == null || events.isEmpty()) {
            return;
        }

        eventsByAggregate.compute(aggregateId, (id, existingEvents) -> {
            if (existingEvents == null) {
                return new ArrayList<>(events);
            }
            List<EventMessage<?>> newEvents = new ArrayList<>(existingEvents);
            newEvents.addAll(events);
            return newEvents;
        });
    }

    @Override
    public List<EventMessage<?>> getEvents(String aggregateId) {
        if (aggregateId == null) {
            return Collections.emptyList();
        }

        List<EventMessage<?>> events = eventsByAggregate.get(aggregateId);
        if (events == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    /**
     * Clears all stored events. Useful for testing.
     */
    public void clear() {
        eventsByAggregate.clear();
    }

    /**
     * Gets all aggregate IDs in the store.
     *
     * @return set of aggregate IDs
     */
    public java.util.Set<String> getAggregateIds() {
        return Collections.unmodifiableSet(eventsByAggregate.keySet());
    }
}
