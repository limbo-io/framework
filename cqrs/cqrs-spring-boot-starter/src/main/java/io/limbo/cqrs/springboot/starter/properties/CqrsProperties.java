package io.limbo.cqrs.springboot.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for CQRS framework.
 */
@ConfigurationProperties(prefix = "limbo.cqrs")
public class CqrsProperties {

    /**
     * Whether to enable CQRS auto-configuration.
     */
    private boolean enabled = true;

    /**
     * Base packages to scan for handlers.
     */
    private String[] basePackages = new String[0];

    /**
     * Command bus configuration.
     */
    private final CommandProperties command = new CommandProperties();

    /**
     * Query bus configuration.
     */
    private final QueryProperties query = new QueryProperties();

    /**
     * Event bus configuration.
     */
    private final EventProperties event = new EventProperties();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String[] getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }

    public CommandProperties getCommand() {
        return command;
    }

    public QueryProperties getQuery() {
        return query;
    }

    public EventProperties getEvent() {
        return event;
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

    /**
     * Event bus specific properties.
     */
    public static class EventProperties {
        /**
         * Whether to enable event bus auto-configuration.
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
