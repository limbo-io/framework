package io.limbo.cqrs.core.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    static class TestEvent {
        private final String value;
        TestEvent(String value) { this.value = value; }
        String getValue() { return value; }
    }
}
