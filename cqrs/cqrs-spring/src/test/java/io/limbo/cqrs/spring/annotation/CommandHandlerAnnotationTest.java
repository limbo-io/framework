package io.limbo.cqrs.spring.annotation;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CommandHandler} annotation.
 */
class CommandHandlerAnnotationTest {

    @Test
    void shouldBePresentOnAnnotatedMethod() throws NoSuchMethodException {
        Method method = SampleHandler.class.getMethod("handle", String.class);

        CommandHandler annotation = method.getAnnotation(CommandHandler.class);

        assertNotNull(annotation);
    }

    @Test
    void shouldNotBePresentOnNonAnnotatedMethod() throws NoSuchMethodException {
        Method method = SampleHandler.class.getMethod("otherMethod");

        CommandHandler annotation = method.getAnnotation(CommandHandler.class);

        assertNull(annotation);
    }

    @Test
    void shouldHaveDefaultValues() throws NoSuchMethodException {
        Method method = SampleHandler.class.getMethod("handle", String.class);

        CommandHandler annotation = method.getAnnotation(CommandHandler.class);

        assertTrue(annotation.annotationType().isAnnotationPresent(java.lang.annotation.Retention.class));
    }

    static class SampleHandler {
        @CommandHandler
        public void handle(String command) {
        }

        public void otherMethod() {
        }
    }
}
