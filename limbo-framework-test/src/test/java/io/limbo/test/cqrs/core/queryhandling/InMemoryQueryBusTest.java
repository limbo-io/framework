package io.limbo.test.cqrs.core.queryhandling;

import io.limbo.cqrs.core.commandhandling.HandlerNotFoundException;
import io.limbo.cqrs.core.queryhandling.HandlerRegistration;
import io.limbo.cqrs.core.queryhandling.IQuery;
import io.limbo.cqrs.core.queryhandling.InMemoryQueryBus;
import io.limbo.cqrs.core.queryhandling.QueryHandler;
import io.limbo.cqrs.core.message.QueryMessage;
import io.limbo.cqrs.core.message.QueryResultMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

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

        queryBus.<TestQuery, String>register(TestQuery.class, q -> "result: " + q.getCriteria());

        String result = queryBus.dispatch(query);
        assertEquals("result: test", result);
    }

    @Test
    void shouldThrowWhenNoHandlerFound() {
        TestQuery query = new TestQuery("test");

        assertThrows(HandlerNotFoundException.class, () -> queryBus.dispatch(query));
    }

    @Test
    void shouldDispatchAsync() throws Exception {
        TestQuery query = new TestQuery("async");

        queryBus.<TestQuery, String>register(TestQuery.class, q -> "async: " + q.getCriteria());

        CompletableFuture<String> future = queryBus.dispatchAsync(query);
        assertEquals("async: async", future.get());
    }

    @Test
    void shouldHandleResultMessage() {
        TestQuery query = new TestQuery("message");

        queryBus.<TestQuery, String>register(TestQuery.class, q -> "msg: " + q.getCriteria());

        QueryMessage<TestQuery, String> message = new QueryMessage<>("test-id", query, String.class);
        QueryResultMessage<String> result = queryBus.dispatch(message);

        assertFalse(result.isExceptional());
        assertEquals("msg: message", result.getPayload());
    }

    @Test
    void shouldUnregisterHandler() {
        TestQuery query = new TestQuery("test");

        HandlerRegistration reg = queryBus.<TestQuery, String>register(TestQuery.class,
                q -> "result");

        // Should work before unregister
        assertEquals("result", queryBus.dispatch(query));

        // Unregister
        reg.unregister();

        // Should throw after unregister
        assertThrows(HandlerNotFoundException.class, () -> queryBus.dispatch(query));
    }

    // Test support classes

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
