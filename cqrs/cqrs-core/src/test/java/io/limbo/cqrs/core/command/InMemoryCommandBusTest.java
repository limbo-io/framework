package io.limbo.cqrs.core.command;

import io.limbo.cqrs.core.handler.HandlerNotFoundException;
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

    @Test
    void shouldExecuteWithTypedResult() {
        CreateUserCommand command = new CreateUserCommand("test@example.com");

        commandBus.register(CreateUserCommand.class, cmd -> new UserId(cmd.getEmail()));

        UserId userId = commandBus.execute(command, UserId.class);
        assertNotNull(userId);
        assertEquals("test@example.com", userId.getId());
    }

    @Test
    void shouldThrowOnTypeMismatch() {
        CreateUserCommand command = new CreateUserCommand("test@example.com");

        commandBus.register(CreateUserCommand.class, cmd -> new UserId(cmd.getEmail()));

        assertThrows(ClassCastException.class, () -> {
            commandBus.execute(command, String.class);
        });
    }

    @Test
    void shouldReturnNullForNullResult() {
        TestCommand command = new TestCommand("test");

        commandBus.register(TestCommand.class, cmd -> null);

        String result = commandBus.execute(command, String.class);
        assertNull(result);
    }

    @Test
    void shouldThrowForNullCommand() {
        assertThrows(IllegalArgumentException.class, () -> {
            commandBus.execute(null);
        });
    }

    @Test
    void shouldThrowForUnregisteredCommand() {
        TestCommand command = new TestCommand("test");

        assertThrows(HandlerNotFoundException.class, () -> {
            commandBus.execute(command);
        });
    }

    @Test
    void shouldThrowForNullCommandType() {
        assertThrows(IllegalArgumentException.class, () -> {
            commandBus.register(null, cmd -> null);
        });
    }

    @Test
    void shouldThrowForNullHandler() {
        assertThrows(IllegalArgumentException.class, () -> {
            commandBus.register(TestCommand.class, null);
        });
    }

    @Test
    void shouldClearRegistrations() {
        TestCommand command = new TestCommand("test");
        commandBus.register(TestCommand.class, cmd -> "result");

        commandBus.clear();

        assertThrows(HandlerNotFoundException.class, () -> {
            commandBus.execute(command);
        });
    }

    static class TestCommand implements ICommand {
        private final String data;
        TestCommand(String data) { this.data = data; }
        String getData() { return data; }
    }

    static class CreateUserCommand implements ICommand {
        private final String email;
        CreateUserCommand(String email) { this.email = email; }
        String getEmail() { return email; }
    }

    static class UserId {
        private final String id;
        UserId(String id) { this.id = id; }
        String getId() { return id; }
    }
}
