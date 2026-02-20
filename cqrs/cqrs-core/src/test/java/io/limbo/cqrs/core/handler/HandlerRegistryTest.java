package io.limbo.cqrs.core.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link HandlerRegistry}.
 */
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
        HandlerNotFoundException exception = assertThrows(
                HandlerNotFoundException.class,
                () -> registry.findHandler(String.class)
        );
        assertTrue(exception.getMessage().contains("java.lang.String"));
    }

    @Test
    void shouldAllowHandlerOverride() throws Exception {
        Handler<String, Integer> handler1 = s -> 1;
        Handler<String, Integer> handler2 = s -> 2;

        registry.register(String.class, handler1);
        registry.register(String.class, handler2);

        Handler<String, ?> found = registry.findHandler(String.class);
        assertEquals(2, found.handle("test"));
    }

    @Test
    void shouldSupportMultipleTypes() throws Exception {
        Handler<String, String> stringHandler = s -> s;
        Handler<Integer, Integer> intHandler = i -> i * 2;

        registry.register(String.class, stringHandler);
        registry.register(Integer.class, intHandler);

        assertEquals("test", registry.findHandler(String.class).handle("test"));
        assertEquals(10, registry.findHandler(Integer.class).handle(5));
    }

    @Test
    void shouldCheckIfHandlerExists() {
        assertFalse(registry.hasHandler(String.class));

        registry.register(String.class, s -> s);

        assertTrue(registry.hasHandler(String.class));
    }

    @Test
    void shouldClearAllRegistrations() {
        registry.register(String.class, s -> s);
        assertTrue(registry.hasHandler(String.class));

        registry.clear();

        assertFalse(registry.hasHandler(String.class));
    }
}
