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
import com.spotify.ffwd.protocol0.Protocol0.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(of = { "has", "proc", "time", "key", "value", "host",
        "tags", "attributes" })
public class Metric {
    private final List<String> EMPTY_TAGS = new ArrayList<String>();
    private final Map<String, String> EMPTY_ATTRIBUTES = new HashMap<String, String>();

    public static final long PROC = 1 << 0;
    public static final long TIME = 1 << 1;
    public static final long KEY = 1 << 2;
    public static final long VALUE = 1 << 3;
    public static final long HOST = 1 << 4;
    public static final long TAGS = 1 << 5;
    public static final long ATTRIBUTES = 1 << 6;

    private final long has;
    @Getter
    private final String proc;
    @Getter
    private final long time;
    @Getter
    private final String key;
    @Getter
    private final double value;
    @Getter
    private final String host;
    @Getter
    private final List<String> tags;
    @Getter
    private final Map<String, String> attributes;

    public Metric() {
        this.has = 0;
        this.proc = null;
        this.time = 0;
        this.key = null;
        this.value = 0;
        this.host = null;
        this.tags = EMPTY_TAGS;
        this.attributes = EMPTY_ATTRIBUTES;
    }

    private boolean test(long n) {
        return (has & n) != 0;
    }

    private long set(long n) {
        return has | n;
    }

    public Metric proc(String proc) {
        return new Metric(set(PROC), proc, time, key, value, host, tags,
                attributes);
    }

    public Metric time(long time) {
        return new Metric(set(TIME), proc, time, key, value, host, tags,
                attributes);
    }

    public Metric key(String key) {
        return new Metric(set(KEY), proc, time, key, value, host, tags,
                attributes);
    }

    public Metric value(double value) {
        return new Metric(set(VALUE), proc, time, key, value, host, tags,
                attributes);
    }

    public Metric host(String host) {
        return new Metric(set(HOST), proc, time, key, value, host, tags,
                attributes);
    }

    public Metric tag(String tag) {
        final List<String> tags = new ArrayList<String>(this.tags);
        tags.add(tag);
        return new Metric(set(TAGS), proc, time, key, value, host, tags,
                attributes);
    }

    public Metric tags(List<String> tags) {
        return new Metric(set(TAGS), proc, time, key, value, host,
                new ArrayList<String>(tags), attributes);
    }

    public Metric attribute(String k, String v) {
        final Map<String, String> attributes = new HashMap<String, String>(
                this.attributes);
        attributes.put(k, v);
        return new Metric(set(ATTRIBUTES), proc, time, key, value, host, tags,
                attributes);
    }

    public Metric attributes(Map<String, String> attributes) {
        return new Metric(set(ATTRIBUTES), proc, time, key, value, host, tags,
                new HashMap<String, String>(attributes));
    }

    public byte[] serialize() {
        final Protocol0.Metric.Builder builder = Protocol0.Metric.newBuilder();

        if (test(PROC))
            builder.setProc(proc);

        if (test(TIME))
            builder.setTime(time);

        if (test(KEY))
            builder.setKey(key);

        if (test(VALUE))
            builder.setValue(value);

        if (test(HOST))
            builder.setHost(host);

        if (test(TAGS)) {
            for (final String tag : tags) {
                builder.addTags(tag);
            }
        }

        if (test(ATTRIBUTES)) {
            for (final Map.Entry<String, String> entry : attributes.entrySet()) {
                if (entry.getKey() == null)
                    continue;

                final Attribute.Builder attributeBuilder = Protocol0.Attribute.newBuilder().setKey(entry.getKey());

                if (entry.getValue() != null)
                    attributeBuilder.setValue(entry.getValue());

                builder.addAttributes(attributeBuilder.build());
            }
        }

        final Protocol0.Metric m = builder.build();
        return Protocol0.Message.newBuilder().setMetric(m).build()
                .toByteArray();
    }
}
