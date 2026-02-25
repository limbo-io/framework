package io.limbo.cqrs.core.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCommandBusTest {

    private InMemoryCommandBus commandBus;

    @BeforeEach
    void setUp() {
        commandBus = new InMemoryCommandBus();
    }

    @Test
    void shouldDispatchToRegisteredHandler() {
        TestCommand command = new TestCommand("test");
        StringBuilder result = new StringBuilder();

        commandBus.register(TestCommand.class, msg -> {
            result.append(msg.getPayload().getData());
            return null;
        });

        commandBus.dispatch(CommandMessage.asCommandMessage(command));
        assertEquals("test", result.toString());
    }

    static class TestCommand {
        private final String data;
        TestCommand(String data) { this.data = data; }
        String getData() { return data; }
    }
}
