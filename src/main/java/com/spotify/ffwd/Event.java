/*
 * Copyright 2019 Spotify AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spotify.ffwd;

import com.spotify.ffwd.protocol0.Protocol0;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    private static final List<String> EMPTY_TAGS = new ArrayList<>();
    private static final Map<String, String> EMPTY_ATTRIBUTES = new HashMap<>();

    private static final long TIME = 1 << 0;
    private static final long KEY = 1 << 1;
    private static final long VALUE = 1 << 2;
    private static final long HOST = 1 << 3;
    private static final long STATE = 1 << 4;
    private static final long DESCRIPTION = 1 << 5;
    private static final long TTL = 1 << 6;
    private static final long TAGS = 1 << 7;
    private static final long ATTRIBUTES = 1 << 8;

    private final long has;
    private final long time;
    private final String key;
    private final double value;
    private final String host;
    private final String state;
    private final String description;
    private final long ttl;
    private final List<String> tags;
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

    public Event(
        long has, long time, String key, double value, String host, String state,
        String description, long ttl, List<String> tags, Map<String, String> attributes
    ) {
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
        final List<String> tags = new ArrayList<>(this.tags);
        tags.add(tag);
        return new Event(set(TAGS), time, key, value, host, state, description,
            ttl, tags, attributes);
    }

    public Event tags(List<String> tags) {
        return new Event(set(TAGS), time, key, value, host, state, description,
            ttl, new ArrayList<>(tags), attributes);
    }

    public Event attribute(String k, String v) {
        final Map<String, String> attributes = new HashMap<>(this.attributes);
        attributes.put(k, v);
        return new Event(set(ATTRIBUTES), time, key, value, host, state,
            description, ttl, tags, attributes);
    }

    public Event attributes(Map<String, String> attributes) {
        return new Event(set(ATTRIBUTES), time, key, value, host, state,
            description, ttl, tags, new HashMap<>(attributes));
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
                if (entry.getKey() == null)
                    continue;

                final Protocol0.Attribute.Builder attributeBuilder =
                    Protocol0.Attribute.newBuilder().setKey(entry.getKey());

                if (entry.getValue() != null)
                    attributeBuilder.setValue(entry.getValue());

                builder.addAttributes(attributeBuilder.build());
            }
        }

        final Protocol0.Event e = builder.build();
        return Protocol0.Message.newBuilder().setEvent(e).build().toByteArray();
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        final Event other = (Event) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        if (this.has != other.has) {
            return false;
        }
        if (this.time != other.time) {
            return false;
        }
        final Object this$key = this.key;
        final Object other$key = other.key;
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
            return false;
        }
        if (Double.compare(this.value, other.value) != 0) {
            return false;
        }
        final Object this$host = this.host;
        final Object other$host = other.host;
        if (this$host == null ? other$host != null : !this$host.equals(other$host)) {
            return false;
        }
        final Object this$state = this.state;
        final Object other$state = other.state;
        if (this$state == null ? other$state != null : !this$state.equals(other$state)) {
            return false;
        }
        final Object this$description = this.description;
        final Object other$description = other.description;
        if (this$description == null ? other$description != null
                                     : !this$description.equals(other$description)) {
            return false;
        }
        if (this.ttl != other.ttl) {
            return false;
        }
        final Object this$tags = this.tags;
        final Object other$tags = other.tags;
        if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) {
            return false;
        }
        final Object this$attributes = this.attributes;
        final Object other$attributes = other.attributes;
        if (this$attributes == null ? other$attributes != null
                                    : !this$attributes.equals(other$attributes)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Event;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $has = this.has;
        result = result * PRIME + (int) ($has >>> 32 ^ $has);
        final long $time = this.time;
        result = result * PRIME + (int) ($time >>> 32 ^ $time);
        final Object $key = this.key;
        result = result * PRIME + ($key == null ? 43 : $key.hashCode());
        final long $value = Double.doubleToLongBits(this.value);
        result = result * PRIME + (int) ($value >>> 32 ^ $value);
        final Object $host = this.host;
        result = result * PRIME + ($host == null ? 43 : $host.hashCode());
        final Object $state = this.state;
        result = result * PRIME + ($state == null ? 43 : $state.hashCode());
        final Object $description = this.description;
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final long $ttl = this.ttl;
        result = result * PRIME + (int) ($ttl >>> 32 ^ $ttl);
        final Object $tags = this.tags;
        result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
        final Object $attributes = this.attributes;
        result = result * PRIME + ($attributes == null ? 43 : $attributes.hashCode());
        return result;
    }

    public long getTime() {
        return this.time;
    }

    public String getKey() {
        return this.key;
    }

    public double getValue() {
        return this.value;
    }

    public String getHost() {
        return this.host;
    }

    public String getState() {
        return this.state;
    }

    public String getDescription() {
        return this.description;
    }

    public long getTtl() {
        return this.ttl;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }
}
