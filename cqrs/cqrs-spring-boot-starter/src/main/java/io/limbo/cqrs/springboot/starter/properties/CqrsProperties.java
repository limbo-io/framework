package io.limbo.cqrs.springboot.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for simplified CQRS framework.
 */
@ConfigurationProperties(prefix = "limbo.cqrs")
public class CqrsProperties {

    /**
     * Whether to enable CQRS auto-configuration.
     */
    private boolean enabled = true;

    /**
     * Command bus configuration.
     */
    private final CommandProperties command = new CommandProperties();

    /**
     * Query bus configuration.
     */
    private final QueryProperties query = new QueryProperties();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public CommandProperties getCommand() {
        return command;
    }

    public QueryProperties getQuery() {
        return query;
    }

    /**
     * Command bus specific properties.
     */
    public static class CommandProperties {
        /**
         * Whether to enable command bus auto-configuration.
         */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * Query bus specific properties.
     */
    public static class QueryProperties {
        /**
         * Whether to enable query bus auto-configuration.
         */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
