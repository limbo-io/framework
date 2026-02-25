package io.limbo.cqrs.core.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventMessageTest {

    @Test
    void shouldCreateEventMessage() {
        TestEvent payload = new TestEvent("test-id");
        EventMessage<TestEvent> message = EventMessage.asEventMessage(payload);

        assertNotNull(message);
        assertNotNull(message.getIdentifier());
        assertEquals(payload, message.getPayload());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void shouldStoreAggregateIdentifier() {
        TestEvent payload = new TestEvent("test-id");
        EventMessage<TestEvent> message = EventMessage.asEventMessage(payload, "aggregate-123");

        assertEquals("aggregate-123", message.getAggregateIdentifier());
    }

    static class TestEvent {
        private final String id;
        TestEvent(String id) { this.id = id; }
        String getId() { return id; }
    }
}
