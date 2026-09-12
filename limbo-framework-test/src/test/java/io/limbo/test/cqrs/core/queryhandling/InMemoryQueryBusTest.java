package io.limbo.test.cqrs.core.queryhandling;

import io.limbo.cqrs.core.message.QueryMessage;
import io.limbo.cqrs.core.message.QueryResultMessage;
import io.limbo.cqrs.core.queryhandling.IQuery;
import io.limbo.cqrs.core.queryhandling.InMemoryQueryBus;
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

        QueryMessage<TestQuery> message = QueryMessage.of(query, "test-id");
        QueryResultMessage<?> result = queryBus.dispatch(message);

        assertFalse(result.isExceptional());
        assertEquals("Result: test", result.getPayload());
    }

    @Test
    void shouldExecuteWithTypedResult() {
        TestQuery query = new TestQuery("test");
        queryBus.register(TestQuery.class, q -> "Result: " + q.getCriteria());

        QueryMessage<TestQuery> message = QueryMessage.of(query, "test-id");
        QueryResultMessage<?> result = queryBus.dispatch(message);

        assertFalse(result.isExceptional());
        assertTrue(result.getPayload() instanceof String);
        assertEquals("Result: test", result.getPayload());
    }

    @Test
    void shouldReturnNullForNullResult() {
        TestQuery query = new TestQuery("test");
        queryBus.register(TestQuery.class, q -> null);

        QueryMessage<TestQuery> message = QueryMessage.of(query, "test-id");
        QueryResultMessage<?> result = queryBus.dispatch(message);

        assertFalse(result.isExceptional());
        assertNull(result.getPayload());
    }

    @Test
    void shouldThrowForNullMessage() {
        assertThrows(IllegalArgumentException.class, () -> {
            queryBus.dispatch(null);
        });
    }

    @Test
    void shouldThrowForUnregisteredQuery() {
        TestQuery query = new TestQuery("test");
        QueryMessage<TestQuery> message = QueryMessage.of(query, "test-id");

        assertThrows(io.limbo.cqrs.core.commandhandling.HandlerNotFoundException.class, () -> {
            queryBus.dispatch(message);
        });
    }

    static class TestQuery implements IQuery<String> {
        private final String criteria;

        TestQuery(String criteria) {
            this.criteria = criteria;
        }

        String getCriteria() {
            return criteria;
        }
    }
}
