/*-
 * -\-\-
 * FastForward Java Client
 * --
 * Copyright (C) 2016 - 2020 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.ffwd.v1;

import com.google.common.base.Objects;
import com.google.protobuf.ByteString;
import com.spotify.ffwd.protocol1.Protocol1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Metric<T> {

  private static final long PROC = 1 << 0;
  private static final long TIME = 1 << 1;
  private static final long KEY = 1 << 2;
  private static final long VALUE = 1 << 3;
  private static final long HOST = 1 << 4;
  private static final long TAGS = 1 << 5;
  private static final long ATTRIBUTES = 1 << 6;

  private final long has;
  private final String proc;
  private final long time;
  private final String key;
  private final Value<T> value;
  private final String host;
  private final List<String> tags;
  private final Map<String, String> attributes;

  public Metric() {
    this.has = 0;
    this.proc = null;
    this.time = 0;
    this.key = null;
    this.value = null;
    this.host = null;
    this.tags = new ArrayList<>();
    this.attributes = new HashMap<>();
  }

  public Metric(
      long has, String proc, long time, String key, Value<T> value, String host,
      List<String> tags, Map<String, String> attributes
  ) {
    this.has = has;
    this.proc = proc;
    this.time = time;
    this.key = key;
    this.value = value;
    this.host = host;
    this.tags = tags;
    this.attributes = attributes;
  }

  private boolean test(long n) {
    return (has & n) != 0;
  }  //TODO This is technically not needed anymore since proto3 doesn't

  private long set(long n) {
    return has | n;
  }

  public Metric<T> proc(String proc) {
    return new Metric<>(set(PROC), proc, time, key, value, host, tags, attributes);
  }

  public Metric<T> time(long time) {
    return new Metric<>(set(TIME), proc, time, key, value, host, tags, attributes);
  }

  public Metric<T> key(String key) {
    return new Metric<>(set(KEY), proc, time, key, value, host, tags, attributes);
  }

  public Metric<T> value(Value<T> value) {
    return new Metric<>(set(VALUE), proc, time, key, value, host, tags, attributes);
  }

  public Metric<T> host(String host) {
    return new Metric<>(set(HOST), proc, time, key, value, host, tags, attributes);
  }

  public Metric<T> tag(String tag) {
    final List<String> tags = new ArrayList<>(this.tags);
    tags.add(tag);
    return new Metric<>(set(TAGS), proc, time, key, value, host, tags, attributes);
  }

  public Metric<T> tags(List<String> tags) {
    return new Metric<>(set(TAGS), proc, time, key, value, host,
        new ArrayList<>(tags), attributes);
  }

  public Metric<T> attribute(String k, String v) {
    final Map<String, String> attributes = new HashMap<>(this.attributes);
    attributes.put(k, v);
    return new Metric<>(set(ATTRIBUTES), proc, time, key, value, host, tags, attributes);
  }

  public Metric<T> attributes(Map<String, String> attributes) {
    return new Metric<>(set(ATTRIBUTES), proc, time, key, value, host, tags,
        new HashMap<>(attributes));
  }

  public byte[] serialize() {
    final Protocol1.Metric.Builder builder = Protocol1.Metric.newBuilder();

    if (test(PROC)) {
      builder.setProc(proc);
    }

    if (test(TIME)) {
      builder.setTime(time);
    }

    if (test(KEY)) {
      builder.setKey(key);
    }

    if (test(VALUE)) {
      if (value instanceof DistributionValue) {
        ByteString byteString = ByteString.copyFrom(((DistributionValue)value).getValue());
        builder.setValue(Protocol1.Value.newBuilder()
            .setDistributionValue(byteString));
      } else if (value instanceof DoubleValue) {
        builder.setValue(Protocol1.Value.newBuilder()
            .setDoubleValue(((DoubleValue) value).getValue()).build());
      } else {
        throw new IllegalArgumentException("Value type not supported");
      }
    }

    if (test(HOST)) {
      builder.setHost(host);
    }

    if (test(TAGS)) {
      for (final String tag : tags) {
        builder.addTags(tag);
      }
    }

    if (test(ATTRIBUTES)) {
      for (final Map.Entry<String, String> entry : attributes.entrySet()) {
        if (entry.getKey() == null) {
          continue;
        }

        final Protocol1.Attribute.Builder attributeBuilder =
            Protocol1.Attribute.newBuilder().setKey(entry.getKey());

        if (entry.getValue() != null) {
          attributeBuilder.setValue(entry.getValue());
        }

        builder.addAttributes(attributeBuilder.build());
      }
    }

    final Protocol1.Metric m = builder.build();
    return Protocol1.Message.newBuilder().setMetric(m).build()
        .toByteArray();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Metric metric = (Metric) o;
    return has == metric.has
           &&
           getTime() == metric.getTime()
           &&
           com.google.common.base.Objects.equal(getProc(), metric.getProc())
           &&
           com.google.common.base.Objects.equal(getKey(), metric.getKey())
           &&
           com.google.common.base.Objects.equal(getValue(), metric.getValue())
           &&
           com.google.common.base.Objects.equal(getHost(), metric.getHost())
           &&
           com.google.common.base.Objects.equal(getTags(), metric.getTags())
           &&
           com.google.common.base.Objects.equal(getAttributes(), metric.getAttributes());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(has, getProc(), getTime(), getKey(), getValue(), getHost(), getTags(),
        getAttributes());
  }

  private boolean canEqual(final Object other) {
    return other instanceof Metric;
  }

  public String getProc() {
    return this.proc;
  }

  public long getTime() {
    return this.time;
  }

  public String getKey() {
    return this.key;
  }

  public Value<T> getValue() {
    return this.value;
  }

  public String getHost() {
    return this.host;
  }

  public List<String> getTags() {
    return this.tags;
  }

  public Map<String, String> getAttributes() {
    return this.attributes;
  }
}
