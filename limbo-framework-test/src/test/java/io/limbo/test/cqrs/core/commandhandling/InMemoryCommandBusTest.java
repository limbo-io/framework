package io.limbo.test.cqrs.core.commandhandling;

import io.limbo.cqrs.core.commandhandling.CommandHandler;
import io.limbo.cqrs.core.commandhandling.HandlerRegistration;
import io.limbo.cqrs.core.commandhandling.ICommand;
import io.limbo.cqrs.core.commandhandling.InMemoryCommandBus;
import io.limbo.cqrs.core.message.CommandMessage;
import io.limbo.cqrs.core.message.CommandResultMessage;
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

        commandBus.<TestCommand, String>register(TestCommand.class, cmd -> "result: " + cmd.getData());

        CommandMessage<TestCommand> message = CommandMessage.of(command, "test-id");
        CommandResultMessage<?> result = commandBus.dispatch(message);

        assertFalse(result.isExceptional());
        assertEquals("result: test", result.getPayload());
    }

    @Test
    void shouldReturnHandlerResult() {
        TestCommand command = new TestCommand("input");

        commandBus.register(TestCommand.class, cmd -> "result: " + cmd.getData());

        CommandMessage<TestCommand> message = CommandMessage.of(command, "test-id");
        CommandResultMessage<?> result = commandBus.dispatch(message);

        assertFalse(result.isExceptional());
        assertEquals("result: input", result.getPayload());
    }

    @Test
    void shouldExecuteWithTypedResult() {
        CreateUserCommand command = new CreateUserCommand("test@example.com");

        commandBus.<CreateUserCommand, UserId>register(CreateUserCommand.class, cmd -> new UserId(cmd.getEmail()));

        CommandMessage<CreateUserCommand> message = CommandMessage.of(command, "test-id");
        CommandResultMessage<?> result = commandBus.dispatch(message);

        assertFalse(result.isExceptional());
        assertTrue(result.getPayload() instanceof UserId);
        UserId userId = (UserId) result.getPayload();
        assertEquals("test@example.com", userId.getId());
    }

    @Test
    void shouldReturnNullForNullResult() {
        TestCommand command = new TestCommand("test");

        @SuppressWarnings({"unchecked", "rawtypes"})
        CommandHandler handler = cmd -> null;
        commandBus.register(TestCommand.class, handler);

        CommandMessage<TestCommand> message = CommandMessage.of(command, "test-id");
        CommandResultMessage<?> result = commandBus.dispatch(message);

        assertFalse(result.isExceptional());
        assertNull(result.getPayload());
    }

    @Test
    void shouldThrowForNullMessage() {
        assertThrows(IllegalArgumentException.class, () -> {
            commandBus.dispatch(null);
        });
    }

    @Test
    void shouldThrowForUnregisteredCommand() {
        TestCommand command = new TestCommand("test");
        CommandMessage<TestCommand> message = CommandMessage.of(command, "test-id");

        assertThrows(io.limbo.cqrs.core.commandhandling.HandlerNotFoundException.class, () -> {
            commandBus.dispatch(message);
        });
    }

    @Test
    void shouldThrowForNullCommandType() {
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings({"unchecked", "rawtypes"})
            CommandHandler handler = cmd -> null;
            commandBus.register(null, handler);
        });
    }

    @Test
    void shouldThrowForNullHandler() {
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings({"unchecked", "rawtypes"})
            CommandHandler handler = null;
            commandBus.register(TestCommand.class, handler);
        });
    }

    @Test
    void shouldThrowForNullRegistration() {
        assertThrows(IllegalArgumentException.class, () -> {
            commandBus.register((HandlerRegistration<?, ?>) null);
        });
    }

    @Test
    void shouldClearRegistrations() {
        TestCommand command = new TestCommand("test");
        commandBus.<TestCommand, String>register(TestCommand.class, cmd -> "result");

        commandBus.clear();

        CommandMessage<TestCommand> message = CommandMessage.of(command, "test-id");
        assertThrows(io.limbo.cqrs.core.commandhandling.HandlerNotFoundException.class, () -> {
            commandBus.dispatch(message);
        });
    }

    static class TestCommand implements ICommand<String> {
        private final String data;

        TestCommand(String data) {
            this.data = data;
        }

        String getData() {
            return data;
        }
    }

    static class CreateUserCommand implements ICommand<UserId> {
        private final String email;

        CreateUserCommand(String email) {
            this.email = email;
        }

        String getEmail() {
            return email;
        }
    }

    static class UserId {
        private final String id;

        UserId(String id) {
            this.id = id;
        }

        String getId() {
            return id;
        }
    }
}
