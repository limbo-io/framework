package io.limbo.test.cqrs.spring;

import io.limbo.cqrs.core.commandhandling.ICommand;
import io.limbo.cqrs.spring.annotation.CommandHandler;
import org.springframework.stereotype.Component;

/**
 * Sample command handler for testing.
 */
@Component
public class SampleCommandHandler {

    @CommandHandler
    public TestResult handle(TestCommand command) {
        return new TestResult(command.getData());
    }

    /**
     * Test command.
     */
    public static class TestCommand implements ICommand<TestResult> {
        private final String data;

        public TestCommand(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }

    /**
     * Test result.
     */
    public static class TestResult {
        private final String value;

        public TestResult(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
