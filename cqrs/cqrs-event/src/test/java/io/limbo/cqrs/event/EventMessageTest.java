package io.limbo.cqrs.event;

import io.limbo.cqrs.core.message.MessageIdentifier;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link EventMessage}.
 */
class EventMessageTest {

    @Test
    void shouldCreateEventMessage() {
        TestEvent payload = new TestEvent("test-id");

        EventMessage<TestEvent> message = EventMessage.asEventMessage(payload);

        assertNotNull(message);
        assertNotNull(message.getIdentifier());
        assertEquals(payload, message.getPayload());
        assertEquals(TestEvent.class, message.getPayloadType());
    }

    @Test
    void shouldGenerateTimestamp() {
        TestEvent payload = new TestEvent("test-id");

        EventMessage<TestEvent> message = EventMessage.asEventMessage(payload);

        assertNotNull(message.getTimestamp());
        Instant now = Instant.now();
        assertTrue(message.getTimestamp().isBefore(now) || message.getTimestamp().equals(now));
    }

    @Test
    void shouldStoreAggregateIdentifier() {
        TestEvent payload = new TestEvent("aggregate-123");

        EventMessage<TestEvent> message = EventMessage.asEventMessage(payload, "aggregate-123");

        assertEquals("aggregate-123", message.getAggregateIdentifier());
    }

    @Test
    void shouldAllowNullAggregateIdentifier() {
        TestEvent payload = new TestEvent("test-id");

        EventMessage<TestEvent> message = EventMessage.asEventMessage(payload);

        assertNull(message.getAggregateIdentifier());
    }

    @Test
    void shouldCreateWithMetadata() {
        TestEvent payload = new TestEvent("test-id");

        EventMessage<TestEvent> message = EventMessage.asEventMessage(payload)
                .withMetadata("key", "value");

        assertEquals("value", message.getMetadata().get("key"));
    }

    @Test
    void shouldReturnNewInstanceWithMetadata() {
        TestEvent payload = new TestEvent("test-id");
        EventMessage<TestEvent> original = EventMessage.asEventMessage(payload);

        EventMessage<TestEvent> withMetadata = original.withMetadata("key", "value");

        assertNotSame(original, withMetadata);
        assertNull(original.getMetadata().get("key"));
        assertEquals("value", withMetadata.getMetadata().get("key"));
    }

    @Test
    void shouldPreserveExistingMetadataWhenAdding() {
        TestEvent payload = new TestEvent("test-id");
        EventMessage<TestEvent> message = EventMessage.asEventMessage(payload)
                .withMetadata("key1", "value1")
                .withMetadata("key2", "value2");

        assertEquals("value1", message.getMetadata().get("key1"));
        assertEquals("value2", message.getMetadata().get("key2"));
    }

    private static class TestEvent {
        private final String id;

        TestEvent(String id) {
            this.id = id;
        }

        String getId() {
            return id;
        }
    }
}
