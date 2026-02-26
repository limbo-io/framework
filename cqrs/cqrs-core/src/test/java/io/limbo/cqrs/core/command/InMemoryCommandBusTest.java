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
    void shouldExecuteRegisteredHandler() {
        TestCommand command = new TestCommand("test");
        StringBuilder result = new StringBuilder();

        commandBus.register(TestCommand.class, cmd -> {
            result.append(cmd.getData());
            return null;
        });

        commandBus.execute(command);
        assertEquals("test", result.toString());
    }

    @Test
    void shouldReturnHandlerResult() {
        TestCommand command = new TestCommand("input");

        commandBus.register(TestCommand.class, cmd -> "result: " + cmd.getData());

        Object result = commandBus.execute(command);
        assertEquals("result: input", result);
    }

    static class TestCommand implements Command {
        private final String data;
        TestCommand(String data) { this.data = data; }
        String getData() { return data; }
    }
}
