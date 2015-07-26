package eu.toolchain.ffwd;

import java.util.List;
import java.util.Map;

public class Batch {
    final Map<String, String> attributes;
    final List<Event> events;
    final List<Metric> metrics;

    public Batch(Map<String, String> attributes, List<Event> events, List<Metric> metrics) {
        this.attributes = checkNotNull(attributes, "attributes");
        this.events = checkNotNull(events, "events");
        this.metrics = checkNotNull(metrics, "metrics");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + attributes.hashCode();
        result = prime * result + events.hashCode();
        result = prime * result + metrics.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Batch other = (Batch) obj;

        if (!attributes.equals(other.attributes)) {
            return false;
        }

        if (!events.equals(other.events)) {
            return false;
        }

        if (!metrics.equals(other.metrics)) {
            return false;
        }

        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append("Batch(");
        builder.append("attributes=").append(attributes).append(", ");
        builder.append("events=").append(events).append(", ");
        builder.append("metrics=").append(metrics);
        builder.append(")");

        return builder.toString();
    }

    private static <T> T checkNotNull(T object, String name) {
        if (object == null) {
            throw new NullPointerException(name);
        }

        return object;
    }
}