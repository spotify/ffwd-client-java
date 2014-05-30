package eu.toolchain.ffwd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.spotify.ffwd.protocol0.Protocol0;

@RequiredArgsConstructor
@EqualsAndHashCode(of = { "has", "time", "key", "value", "host", "state",
        "description", "ttl", "tags", "attributes" })
public class Event {
    private static final List<String> EMPTY_TAGS = new ArrayList<String>();
    private static final Map<String, String> EMPTY_ATTRIBUTES = new HashMap<String, String>();

    public static final long TIME = 1 << 0;
    public static final long KEY = 1 << 1;
    public static final long VALUE = 1 << 2;
    public static final long HOST = 1 << 3;
    public static final long STATE = 1 << 4;
    public static final long DESCRIPTION = 1 << 5;
    public static final long TTL = 1 << 6;
    public static final long TAGS = 1 << 7;
    public static final long ATTRIBUTES = 1 << 8;

    private final long has;
    @Getter
    private final long time;
    @Getter
    private final String key;
    @Getter
    private final double value;
    @Getter
    private final String host;
    @Getter
    private final String state;
    @Getter
    private final String description;
    @Getter
    private final long ttl;
    @Getter
    private final List<String> tags;
    @Getter
    private final Map<String, String> attributes;

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

    private boolean test(long n) {
        return (has & n) != 0;
    }

    private long set(long n) {
        return has | n;
    }

    public Event time(long time) {
        return new Event(set(TIME), time, key, value, host, state, description,
                ttl, tags, attributes);
    }

    public Event key(String key) {
        return new Event(set(KEY), time, key, value, host, state, description,
                ttl, tags, attributes);
    }

    public Event value(double value) {
        return new Event(set(VALUE), time, key, value, host, state,
                description, ttl, tags, attributes);
    }

    public Event host(String host) {
        return new Event(set(HOST), time, key, value, host, state, description,
                ttl, tags, attributes);
    }

    public Event state(String state) {
        return new Event(set(STATE), time, key, value, host, state,
                description, ttl, tags, attributes);
    }

    public Event description(String description) {
        return new Event(set(DESCRIPTION), time, key, value, host, state,
                description, ttl, tags, attributes);
    }

    public Event ttl(long ttl) {
        return new Event(set(TTL), time, key, value, host, state, description,
                ttl, tags, attributes);
    }

    public Event tag(String tag) {
        final List<String> tags = new ArrayList<String>(this.tags);
        tags.add(tag);
        return new Event(set(TAGS), time, key, value, host, state, description,
                ttl, tags, attributes);
    }

    public Event tags(List<String> tags) {
        return new Event(set(TAGS), time, key, value, host, state, description,
                ttl, new ArrayList<String>(tags), attributes);
    }

    public Event attribute(String k, String v) {
        final Map<String, String> attributes = new HashMap<String, String>(
                this.attributes);
        attributes.put(k, v);
        return new Event(set(ATTRIBUTES), time, key, value, host, state,
                description, ttl, tags, attributes);
    }

    public Event attributes(Map<String, String> attributes) {
        return new Event(set(ATTRIBUTES), time, key, value, host, state,
                description, ttl, tags, new HashMap<String, String>(attributes));
    }

    public byte[] serialize() {
        final Protocol0.Event.Builder builder = Protocol0.Event.newBuilder();

        if (test(TIME))
            builder.setTime(time);

        if (test(KEY))
            builder.setKey(key);

        if (test(VALUE))
            builder.setValue(value);

        if (test(HOST))
            builder.setHost(host);

        if (test(STATE))
            builder.setState(host);

        if (test(DESCRIPTION))
            builder.setDescription(description);

        if (test(TTL))
            builder.setTtl(ttl);

        if (test(TAGS)) {
            for (final String tag : tags) {
                builder.addTags(tag);
            }
        }

        if (test(ATTRIBUTES)) {
            for (final Map.Entry<String, String> entry : attributes.entrySet()) {
                builder.addAttributes(Protocol0.Attribute.newBuilder()
                        .setKey(entry.getKey()).setValue(entry.getValue())
                        .build());
            }
        }

        final Protocol0.Event e = builder.build();
        return Protocol0.Message.newBuilder().setEvent(e).build().toByteArray();
    }
}
