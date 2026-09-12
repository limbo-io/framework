package io.limbo.test.cqrs.core.commandhandling;

import io.limbo.cqrs.core.commandhandling.CommandHandler;
import io.limbo.cqrs.core.commandhandling.HandlerNotFoundException;
import io.limbo.cqrs.core.commandhandling.HandlerRegistration;
import io.limbo.cqrs.core.commandhandling.ICommand;
import io.limbo.cqrs.core.commandhandling.InMemoryCommandBus;
import io.limbo.cqrs.core.message.CommandMessage;
import io.limbo.cqrs.core.message.CommandResultMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

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

        String result = commandBus.dispatch(command);
        assertEquals("result: test", result);
    }

    @Test
    void shouldThrowWhenNoHandlerFound() {
        TestCommand command = new TestCommand("test");

        assertThrows(HandlerNotFoundException.class, () -> commandBus.dispatch(command));
    }

    @Test
    void shouldDispatchAsync() throws Exception {
        TestCommand command = new TestCommand("async");

        commandBus.<TestCommand, String>register(TestCommand.class, cmd -> "async: " + cmd.getData());

        CompletableFuture<String> future = commandBus.dispatchAsync(command);
        assertEquals("async: async", future.get());
    }

    @Test
    void shouldHandleResultMessage() {
        TestCommand command = new TestCommand("message");

        commandBus.<TestCommand, String>register(TestCommand.class, cmd -> "msg: " + cmd.getData());

        CommandMessage<TestCommand> message = new CommandMessage<>("test-id", command);
        CommandResultMessage<String> result = commandBus.dispatch(message);

        assertFalse(result.isExceptional());
        assertEquals("msg: message", result.getPayload());
    }

    @Test
    void shouldHandleExceptionInResultMessage() {
        TestCommand command = new TestCommand("error");

        commandBus.<TestCommand, String>register(TestCommand.class, cmd -> {
            throw new RuntimeException("Test error");
        });

        CommandMessage<TestCommand> message = new CommandMessage<>(command);
        CommandResultMessage<String> result = commandBus.dispatch(message);

        assertTrue(result.isExceptional());
        assertNotNull(result.getException());
        assertEquals("Test error", result.getException().getMessage());
    }

    @Test
    void shouldUnregisterHandler() {
        TestCommand command = new TestCommand("test");

        HandlerRegistration reg = commandBus.<TestCommand, String>register(TestCommand.class,
                cmd -> "result");

        // Should work before unregister
        assertEquals("result", commandBus.dispatch(command));

        // Unregister
        reg.unregister();

        // Should throw after unregister
        assertThrows(HandlerNotFoundException.class, () -> commandBus.dispatch(command));
    }

    // Test support classes

    static class TestCommand implements ICommand {
        private final String data;

        TestCommand(String data) {
            this.data = data;
        }

        String getData() {
            return data;
        }
    }
}
