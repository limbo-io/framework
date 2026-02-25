package io.limbo.cqrs.core.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryMessageTest {

    @Test
    void shouldCreateQueryMessage() {
        TestQuery payload = new TestQuery("criteria");
        QueryMessage<TestQuery> message = QueryMessage.asQueryMessage(payload);

        assertNotNull(message);
        assertNotNull(message.getIdentifier());
        assertEquals(payload, message.getPayload());
    }

    @Test
    void shouldAddMetadata() {
        TestQuery payload = new TestQuery("test");
        QueryMessage<TestQuery> message = QueryMessage.asQueryMessage(payload);
        QueryMessage<TestQuery> withMeta = message.withMetadata("key", "value");

        assertEquals("value", withMeta.getMetadata().get("key"));
    }

    static class TestQuery {
        private final String criteria;
        TestQuery(String criteria) { this.criteria = criteria; }
        String getCriteria() { return criteria; }
    }
}
