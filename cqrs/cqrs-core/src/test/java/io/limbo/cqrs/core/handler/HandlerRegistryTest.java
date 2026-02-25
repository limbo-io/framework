package io.limbo.cqrs.core.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HandlerRegistryTest {

    private HandlerRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new HandlerRegistry<>();
    }

    @Test
    void shouldRegisterAndFindHandler() {
        Handler<String, Integer> handler = Integer::parseInt;
        registry.register(String.class, handler);

        Handler<String, ?> found = registry.findHandler(String.class);
        assertSame(handler, found);
    }

    @Test
    void shouldThrowWhenHandlerNotFound() {
        assertThrows(HandlerNotFoundException.class, () -> registry.findHandler(String.class));
    }

    @Test
    void shouldCheckIfHandlerExists() {
        assertFalse(registry.hasHandler(String.class));
        registry.register(String.class, s -> s);
        assertTrue(registry.hasHandler(String.class));
    }
}
