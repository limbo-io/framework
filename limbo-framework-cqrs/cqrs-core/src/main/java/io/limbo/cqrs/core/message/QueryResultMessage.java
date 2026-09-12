package io.limbo.cqrs.core.message;

/**
 * Result message for queries.
 *
 * @param <R> the result type
 */
public class QueryResultMessage<R> implements ResultMessage<R> {

    private static final long serialVersionUID = 1L;

    private final String identifier;
    private final R payload;
    private final Throwable exception;

    public QueryResultMessage(String identifier, R payload, Throwable exception) {
        this.identifier = identifier;
        this.payload = payload;
        this.exception = exception;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public R getPayload() {
        return payload;
    }

    @Override
    public boolean isExceptional() {
        return exception != null;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    /**
     * Get the result or throw the exception if present.
     *
     * @return the result
     * @throws RuntimeException if an exception occurred
     */
    public R getOrThrow() {
        if (exception != null) {
            if (exception instanceof RuntimeException re) {
                throw re;
            }
            throw new RuntimeException(exception);
        }
        return payload;
    }
}
