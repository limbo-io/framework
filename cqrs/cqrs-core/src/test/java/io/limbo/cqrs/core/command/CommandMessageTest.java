package io.limbo.cqrs.core.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandMessageTest {

    @Test
    void shouldCreateCommandMessage() {
        TestCommand payload = new TestCommand("test-data");
        CommandMessage<TestCommand> message = CommandMessage.asCommandMessage(payload);

        assertNotNull(message);
        assertNotNull(message.getIdentifier());
        assertEquals(payload, message.getPayload());
    }

    @Test
    void shouldAddMetadata() {
        TestCommand payload = new TestCommand("test");
        CommandMessage<TestCommand> message = CommandMessage.asCommandMessage(payload);
        CommandMessage<TestCommand> withMeta = message.withMetadata("key", "value");

        assertEquals("value", withMeta.getMetadata().get("key"));
    }

    static class TestCommand {
        private final String data;
        TestCommand(String data) { this.data = data; }
        String getData() { return data; }
    }
}
