package io.limbo.cqrs.core.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryQueryBusTest {

    private InMemoryQueryBus queryBus;

    @BeforeEach
    void setUp() {
        queryBus = new InMemoryQueryBus();
    }

    @Test
    void shouldExecuteRegisteredHandler() {
        TestQuery query = new TestQuery("test");
        queryBus.register(TestQuery.class, q -> "Result: " + q.getCriteria());

        Object result = queryBus.execute(query);
        assertEquals("Result: test", result);
    }

    @Test
    void shouldExecuteWithTypedResult() {
        TestQuery query = new TestQuery("test");
        queryBus.register(TestQuery.class, q -> "Result: " + q.getCriteria());

        String result = queryBus.execute(query, String.class);
        assertEquals("Result: test", result);
    }

    static class TestQuery implements IQuery {
        private final String criteria;
        TestQuery(String criteria) { this.criteria = criteria; }
        String getCriteria() { return criteria; }
    }
}
