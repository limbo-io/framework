package io.limbo.test.cqrs.spring;

import io.limbo.cqrs.core.queryhandling.IQuery;
import io.limbo.cqrs.spring.annotation.QueryHandler;
import org.springframework.stereotype.Component;

/**
 * Sample query handler for testing.
 */
@Component
public class SampleQueryHandler {

    @QueryHandler
    public TestResult handle(TestQuery query) {
        return new TestResult(query.getCriteria());
    }

    /**
     * Test query.
     */
    public static class TestQuery implements IQuery<TestResult> {
        private final String criteria;

        public TestQuery(String criteria) {
            this.criteria = criteria;
        }

        public String getCriteria() {
            return criteria;
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
