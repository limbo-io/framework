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
    void shouldDispatchToRegisteredHandler() {
        TestQuery query = new TestQuery("test");
        queryBus.register(TestQuery.class, String.class, msg -> "Result: " + msg.getPayload().getCriteria());

        Object result = queryBus.query(QueryMessage.asQueryMessage(query));
        assertEquals("Result: test", result);
    }

    static class TestQuery {
        private final String criteria;
        TestQuery(String criteria) { this.criteria = criteria; }
        String getCriteria() { return criteria; }
    }
}
