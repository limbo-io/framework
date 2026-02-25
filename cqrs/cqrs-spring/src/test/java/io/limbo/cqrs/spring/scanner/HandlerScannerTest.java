package io.limbo.cqrs.spring.scanner;

import io.limbo.cqrs.core.command.Command;
import io.limbo.cqrs.core.event.Event;
import io.limbo.cqrs.core.query.Query;
import io.limbo.cqrs.spring.annotation.CommandHandler;
import io.limbo.cqrs.spring.annotation.EventHandler;
import io.limbo.cqrs.spring.annotation.QueryHandler;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link HandlerScanner}.
 */
class HandlerScannerTest {

    private final HandlerScanner scanner = new HandlerScanner();

    @Test
    void shouldScanCommandHandlers() {
        Set<Class<?>> handlers = scanner.scanCommandHandlers("io.limbo.cqrs.spring.scanner");

        assertTrue(handlers.contains(SampleCommandHandler.class));
    }

    @Test
    void shouldScanEventHandlers() {
        Set<Class<?>> handlers = scanner.scanEventHandlers("io.limbo.cqrs.spring.scanner");

        assertTrue(handlers.contains(SampleEventHandler.class));
    }

    @Test
    void shouldScanQueryHandlers() {
        Set<Class<?>> handlers = scanner.scanQueryHandlers("io.limbo.cqrs.spring.scanner");

        assertTrue(handlers.contains(SampleQueryHandler.class));
    }

    @Test
    void shouldExtractCommandHandlerMethods() {
        List<HandlerScanner.HandlerMethod> methods = scanner.extractHandlerMethods(SampleCommandHandler.class);

        assertEquals(1, methods.size());
        assertEquals(TestCommand.class, methods.get(0).getPayloadType());
        assertEquals(SampleCommandHandler.class, methods.get(0).getBeanClass());
    }

    @Test
    void shouldExtractEventHandlerMethods() {
        List<HandlerScanner.HandlerMethod> methods = scanner.extractHandlerMethods(SampleEventHandler.class);

        assertEquals(1, methods.size());
        assertEquals(TestEvent.class, methods.get(0).getPayloadType());
    }

    @Test
    void shouldExtractQueryHandlerMethods() {
        List<HandlerScanner.HandlerMethod> methods = scanner.extractHandlerMethods(SampleQueryHandler.class);

        assertEquals(1, methods.size());
        assertEquals(TestQuery.class, methods.get(0).getPayloadType());
    }

    // Sample classes for testing
    static class TestCommand implements Command {
    }

    static class TestEvent implements Event {
    }

    static class TestQuery implements Query {
    }

    static class SampleCommandHandler {
        @CommandHandler
        public void handle(TestCommand command) {
        }
    }

    static class SampleEventHandler {
        @EventHandler
        public void on(TestEvent event) {
        }
    }

    static class SampleQueryHandler {
        @QueryHandler
        public String handle(TestQuery query) {
            return "result";
        }
    }
}
