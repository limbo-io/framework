package io.limbo.cqrs.event.store;

import io.limbo.cqrs.event.EventMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link InMemoryEventStore}.
 */
class InMemoryEventStoreTest {

    private InMemoryEventStore eventStore;

    @BeforeEach
    void setUp() {
        eventStore = new InMemoryEventStore();
    }

    @Test
    void shouldAppendAndRetrieveEvents() {
        String aggregateId = "agg-123";
        TestEvent event1 = new TestEvent("data1");
        TestEvent event2 = new TestEvent("data2");

        eventStore.appendEvents(aggregateId, Arrays.asList(
                EventMessage.asEventMessage(event1),
                EventMessage.asEventMessage(event2)
        ));

        List<EventMessage<?>> events = eventStore.getEvents(aggregateId);
        assertEquals(2, events.size());
        assertEquals("data1", ((TestEvent) events.get(0).getPayload()).getData());
        assertEquals("data2", ((TestEvent) events.get(1).getPayload()).getData());
    }

    @Test
    void shouldPreserveEventOrder() {
        String aggregateId = "agg-123";

        for (int i = 0; i < 5; i++) {
            eventStore.appendEvents(aggregateId, Arrays.asList(
                    EventMessage.asEventMessage(new TestEvent("event-" + i))
            ));
        }

        List<EventMessage<?>> events = eventStore.getEvents(aggregateId);
        assertEquals(5, events.size());
        for (int i = 0; i < 5; i++) {
            assertEquals("event-" + i, ((TestEvent) events.get(i).getPayload()).getData());
        }
    }

    @Test
    void shouldReturnEmptyListForUnknownAggregate() {
        List<EventMessage<?>> events = eventStore.getEvents("unknown-id");

        assertTrue(events.isEmpty());
    }

    @Test
    void shouldAppendSingleEvent() {
        String aggregateId = "agg-123";
        TestEvent event = new TestEvent("data");

        eventStore.appendEvent(aggregateId, EventMessage.asEventMessage(event));

        List<EventMessage<?>> events = eventStore.getEvents(aggregateId);
        assertEquals(1, events.size());
        assertEquals("data", ((TestEvent) events.get(0).getPayload()).getData());
    }

    @Test
    void shouldSeparateDifferentAggregates() {
        String aggregateId1 = "agg-1";
        String aggregateId2 = "agg-2";

        eventStore.appendEvents(aggregateId1, Arrays.asList(
                EventMessage.asEventMessage(new TestEvent("event1"))
        ));
        eventStore.appendEvents(aggregateId2, Arrays.asList(
                EventMessage.asEventMessage(new TestEvent("event2"))
        ));

        List<EventMessage<?>> events1 = eventStore.getEvents(aggregateId1);
        List<EventMessage<?>> events2 = eventStore.getEvents(aggregateId2);

        assertEquals(1, events1.size());
        assertEquals(1, events2.size());
        assertEquals("event1", ((TestEvent) events1.get(0).getPayload()).getData());
        assertEquals("event2", ((TestEvent) events2.get(0).getPayload()).getData());
    }

    private static class TestEvent {
        private final String data;

        TestEvent(String data) {
            this.data = data;
        }

        String getData() {
            return data;
        }
    }
}
