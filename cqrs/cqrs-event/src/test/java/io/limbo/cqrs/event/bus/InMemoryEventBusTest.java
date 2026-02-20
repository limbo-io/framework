package io.limbo.cqrs.event.bus;

import io.limbo.cqrs.event.EventMessage;
import io.limbo.cqrs.event.handler.EventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link InMemoryEventBus}.
 */
class InMemoryEventBusTest {

    private InMemoryEventBus eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new InMemoryEventBus();
    }

    @Test
    void shouldPublishEventToSubscribedHandler() {
        List<String> received = new ArrayList<>();
        TestEvent event = new TestEvent("test");

        eventBus.subscribe(TestEvent.class, evt -> received.add(evt.getPayload().getValue()));
        eventBus.publish(EventMessage.asEventMessage(event));

        assertEquals(1, received.size());
        assertEquals("test", received.get(0));
    }

    @Test
    void shouldPublishToMultipleHandlers() {
        List<String> received1 = new ArrayList<>();
        List<String> received2 = new ArrayList<>();
        TestEvent event = new TestEvent("test");

        eventBus.subscribe(TestEvent.class, (EventHandler<TestEvent>) evt -> received1.add(evt.getPayload().getValue()));
        eventBus.subscribe(TestEvent.class, (EventHandler<TestEvent>) evt -> received2.add(evt.getPayload().getValue()));
        eventBus.publish(EventMessage.asEventMessage(event));

        assertEquals(1, received1.size());
        assertEquals(1, received2.size());
        assertEquals("test", received1.get(0));
        assertEquals("test", received2.get(0));
    }

    @Test
    void shouldNotPublishToUnsubscribedEventTypes() {
        List<String> received = new ArrayList<>();
        TestEvent event = new TestEvent("test");

        eventBus.subscribe(OtherEvent.class, (EventHandler<OtherEvent>) evt -> received.add("wrong"));
        eventBus.publish(EventMessage.asEventMessage(event));

        assertTrue(received.isEmpty());
    }

    @Test
    void shouldPublishAllEvents() {
        List<String> received = new ArrayList<>();

        eventBus.subscribe(TestEvent.class, (EventHandler<TestEvent>) evt -> received.add(evt.getPayload().getValue()));
        eventBus.publishAll(java.util.Arrays.asList(
                EventMessage.asEventMessage(new TestEvent("event1")),
                EventMessage.asEventMessage(new TestEvent("event2"))
        ));

        assertEquals(2, received.size());
        assertEquals("event1", received.get(0));
        assertEquals("event2", received.get(1));
    }

    @Test
    void shouldAllowUnsubscribe() {
        List<String> received = new ArrayList<>();
        TestEvent event = new TestEvent("test");

        Runnable unsubscribe = eventBus.subscribe(TestEvent.class,
                (EventHandler<TestEvent>) evt -> received.add(evt.getPayload().getValue()));
        unsubscribe.run();
        eventBus.publish(EventMessage.asEventMessage(event));

        assertTrue(received.isEmpty());
    }

    private static class TestEvent {
        private final String value;

        TestEvent(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    private static class OtherEvent {
    }
}
