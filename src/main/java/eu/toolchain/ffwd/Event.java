package eu.toolchain.ffwd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    public static final List<String> EMPTY_TAGS = new ArrayList<>();
    public static final Map<String, String> EMPTY_ATTRIBUTES = new HashMap<>();

    public static final long TIME = 1 << 0;
    public static final long KEY = 1 << 1;
    public static final long VALUE = 1 << 2;
    public static final long HOST = 1 << 3;
    public static final long STATE = 1 << 4;
    public static final long DESCRIPTION = 1 << 5;
    public static final long TTL = 1 << 6;
    public static final long TAGS = 1 << 7;
    public static final long ATTRIBUTES = 1 << 8;

    final long has;

    final long time;
    final String key;
    final double value;
    final String host;
    final String state;
    final String description;
    final long ttl;
    final List<String> tags;
    final Map<String, String> attributes;

    public Event(long has, long time, String key, double value, String host, String state, String description, long ttl, List<String> tags, Map<String, String> attributes) {
        this.has = has;
        this.time = time;
        this.key = key;
        this.value = value;
        this.host = host;
        this.state = state;
        this.description = description;
        this.ttl = ttl;
        this.tags = tags;
        this.attributes = attributes;
    }

    public Event() {
        this.has = 0;
        this.time = 0;
        this.key = null;
        this.value = 0;
        this.host = null;
        this.state = null;
        this.description = null;
        this.ttl = 0;
        this.tags = EMPTY_TAGS;
        this.attributes = EMPTY_ATTRIBUTES;
    }

    boolean test(long n) {
        return (has & n) != 0;
    }

    void check(long field, String name) {
        if ((has & field) == 0) {
            throw new IllegalStateException(name);
        }
    }

    private long set(long n) {
        return has | n;
    }

    public Event time(long time) {
        return new Event(set(TIME), time, key, value, host, state, description,
                ttl, tags, attributes);
    }

    public long getTime() {
        check(TIME, "time");
        return time;
    }

    public Event key(String key) {
        checkNotNull(key, "key");

        return new Event(set(KEY), time, key, value, host, state, description,
                ttl, tags, attributes);
    }

    public String getKey() {
        check(KEY, "key");
        return key;
    }

    public Event value(double value) {
        return new Event(set(VALUE), time, key, value, host, state,
                description, ttl, tags, attributes);
    }

    public double getValue() {
        check(VALUE, "value");
        return value;
    }

    public Event host(String host) {
        checkNotNull(host, "host");

        return new Event(set(HOST), time, key, value, host, state, description,
                ttl, tags, attributes);
    }

    public String getHost() {
        check(HOST, "host");
        return host;
    }

    public Event state(String state) {
        checkNotNull(state, "state");

        return new Event(set(STATE), time, key, value, host, state,
                description, ttl, tags, attributes);
    }

    public String getState() {
        check(STATE, "state");
        return state;
    }

    public Event description(String description) {
        checkNotNull(description, "description");

        return new Event(set(DESCRIPTION), time, key, value, host, state,
                description, ttl, tags, attributes);
    }

    public String getDescription() {
        check(DESCRIPTION, "description");
        return description;
    }

    public Event ttl(long ttl) {
        return new Event(set(TTL), time, key, value, host, state, description,
                ttl, tags, attributes);
    }

    public long getTtl() {
        check(TTL, "ttl");
        return ttl;
    }

    public Event tag(String tag) {
        checkNotNull(tag, "tag");

        final List<String> tags = new ArrayList<String>(this.tags);
        tags.add(tag);
        return new Event(set(TAGS), time, key, value, host, state, description,
                ttl, tags, attributes);
    }

    public Event tags(List<String> tags) {
        checkNotNull(tags, "tags");

        final ArrayList<String> copy = new ArrayList<String>(tags);
        return new Event(set(TAGS), time, key, value, host, state, description,
                ttl, copy, attributes);
    }

    public List<String> getTags() {
        check(TAGS, "tags");
        return tags;
    }

    public Event attribute(String k, String v) {
        checkNotNull(k, "k");
        checkNotNull(v, "v");

        final Map<String, String> attributes = new HashMap<String, String>(
                this.attributes);
        attributes.put(k, v);
        return new Event(set(ATTRIBUTES), time, key, value, host, state,
                description, ttl, tags, attributes);
    }

    public Event attributes(Map<String, String> attributes) {
        checkNotNull(attributes, "attributes");

        return new Event(set(ATTRIBUTES), time, key, value, host, state,
                description, ttl, tags, new HashMap<String, String>(attributes));
    }

    public Map<String, String> getAttributes() {
        check(ATTRIBUTES, "attributes");
        return attributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = (int) (has ^ (has >>> 32));

        if (test(HOST)) {
            result = prime * result + host.hashCode();
        }

        if (test(KEY)) {
            result = prime * result + key.hashCode();
        }

        if (test(TIME)) {
            result = prime * result + (int) (time ^ (time >>> 32));
        }

        if (test(TTL)) {
            result = prime * result + (int) (ttl ^ (ttl >>> 32));
        }

        if (test(STATE)) {
            result = prime * result + state.hashCode();
        }

        if (test(DESCRIPTION)) {
            result = prime * result + description.hashCode();
        }

        if (test(TAGS)) {
            result = prime * result + tags.hashCode();
        }

        if (test(ATTRIBUTES)) {
            result = prime * result + attributes.hashCode();
        }

        if (test(VALUE)) {
            long temp;
            temp = Double.doubleToLongBits(value);
            result = prime * result + (int) (temp ^ (temp >>> 32));
        }

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

        final Event other = (Event) obj;

        // different field configurations
        if (has != other.has) {
            return false;
        }

        if (test(HOST) && !host.equals(other.host)) {
            return false;
        }

        if (test(KEY) && !key.equals(other.key)) {
            return false;
        }

        if (test(TIME) && time != other.time) {
            return false;
        }

        if (test(STATE) && !state.equals(other.state)) {
            return false;
        }

        if (test(DESCRIPTION) && !description.equals(other.description)) {
            return false;
        }

        if (test(TTL) && ttl != other.ttl) {
            return false;
        }

        if (test(TAGS) && !tags.equals(other.tags)) {
            return false;
        }

        if (test(ATTRIBUTES) && !attributes.equals(other.attributes)) {
            return false;
        }

        if (test(VALUE) && Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
            return false;
        }

        return true;
    }

    private static <T> void checkNotNull(T object, String name) {
        if (object == null) {
            throw new NullPointerException(name);
        }
    }

    public static Event empty() {
        return new Event();
    }
}