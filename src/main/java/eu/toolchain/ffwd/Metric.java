package eu.toolchain.ffwd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Metric {
    public static final List<String> EMPTY_TAGS = new ArrayList<>();
    public static final Map<String, String> EMPTY_ATTRIBUTES = new HashMap<>();

    private static final Metric EMPTY = new Metric();

    public static final long PROC = 1 << 0;
    public static final long TIME = 1 << 1;
    public static final long KEY = 1 << 2;
    public static final long VALUE = 1 << 3;
    public static final long HOST = 1 << 4;
    public static final long TAGS = 1 << 5;
    public static final long ATTRIBUTES = 1 << 6;

    final long has;

    final String proc;
    final long time;
    final String key;
    final double value;
    final String host;
    final List<String> tags;
    final Map<String, String> attributes;

    private Metric(long has, String proc, long time, String key, double value, String host, List<String> tags, Map<String, String> attributes) {
        this.has = has;
        this.proc = proc;
        this.time = time;
        this.key = key;
        this.value = value;
        this.host = host;
        this.tags = tags;
        this.attributes = attributes;
    }

    private Metric() {
        this.has = 0;
        this.proc = null;
        this.time = 0;
        this.key = null;
        this.value = 0;
        this.host = null;
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

    public Metric proc(String proc) {
        checkNotNull(proc, proc);

        return new Metric(set(PROC), proc, time, key, value, host, tags,
                attributes);
    }

    public String getProc() {
        check(PROC, "proc");
        return proc;
    }

    public Metric time(long time) {
        return new Metric(set(TIME), proc, time, key, value, host, tags,
                attributes);
    }

    public long getTime() {
        check(TIME, "time");
        return time;
    }

    public Metric key(String key) {
        checkNotNull(key, "key");

        return new Metric(set(KEY), proc, time, key, value, host, tags,
                attributes);
    }

    public String getKey() {
        check(KEY, "key");
        return key;
    }

    public Metric value(double value) {
        return new Metric(set(VALUE), proc, time, key, value, host, tags,
                attributes);
    }

    public double getValue() {
        check(VALUE, "value");
        return value;
    }

    public Metric host(String host) {
        checkNotNull(host, "host");

        return new Metric(set(HOST), proc, time, key, value, host, tags,
                attributes);
    }

    public String getHost() {
        check(HOST, "host");
        return host;
    }

    public Metric tag(String tag) {
        checkNotNull(tag, "tag");

        final List<String> tags = new ArrayList<String>(this.tags);
        tags.add(tag);
        return new Metric(set(TAGS), proc, time, key, value, host, tags,
                attributes);
    }

    public Metric tags(List<String> tags) {
        checkNotNull(tags, "tags");

        return new Metric(set(TAGS), proc, time, key, value, host,
                new ArrayList<String>(tags), attributes);
    }

    public List<String> getTag() {
        check(TAGS, "tags");
        return Collections.unmodifiableList(tags);
    }

    public Metric attribute(String k, String v) {
        checkNotNull(k, "k");
        checkNotNull(v, "v");

        final Map<String, String> attributes = new HashMap<String, String>(
                this.attributes);
        attributes.put(k, v);
        return new Metric(set(ATTRIBUTES), proc, time, key, value, host, tags,
                attributes);
    }

    public Metric attributes(Map<String, String> attributes) {
        checkNotNull(attributes, "attributes");

        return new Metric(set(ATTRIBUTES), proc, time, key, value, host, tags,
                new HashMap<String, String>(attributes));
    }

    public Map<String, String> getAttributes() {
        check(ATTRIBUTES, "attributes");
        return Collections.unmodifiableMap(attributes);
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

        final Metric other = (Metric) obj;

        if (has != other.has) {
            return false;
        }

        if (test(HOST) && !host.equals(other.host)) {
            return false;
        }

        if (test(KEY) && !key.equals(other.key)) {
            return false;
        }

        if (test(PROC) && !proc.equals(other.proc)) {
            return false;
        }

        if (test(TIME) && time != other.time) {
            return false;
        }

        if (test(VALUE) && Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
            return false;
        }

        if (test(TAGS) && !tags.equals(other.tags)) {
          return false;
        }

        if (test(ATTRIBUTES) && !attributes.equals(other.attributes)) {
            return false;
        }

        return true;
    }

    static <T> void checkNotNull(T object, String name) {
        if (object == null) {
            throw new NullPointerException(name);
        }
    }

    public static Metric empty() {
        return EMPTY;
    }

    public static Metric metric(Map<String, String> attributes, double value) {
        return new Metric(ATTRIBUTES | VALUE, null, 0, null, value, null, null,
                attributes);
    }
}