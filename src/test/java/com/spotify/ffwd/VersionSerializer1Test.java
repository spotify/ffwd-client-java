package com.spotify.ffwd;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import eu.toolchain.ffwd.Batch;
import eu.toolchain.ffwd.BatchOption;
import eu.toolchain.ffwd.Event;
import eu.toolchain.ffwd.Metric;
import eu.toolchain.ffwd.VersionSerializer1;

public class VersionSerializer1Test {
    final VersionSerializer1 serializer = new VersionSerializer1();

    private void roundtrip(Metric e) throws IOException {
        final byte[] body = serializer.writeMetric(e);
        final Metric o;

        try (final InputStream input = new ByteArrayInputStream(body)) {
            o = serializer.readMetric(input);
        }

        assertEquals(e, o);
    }

    private void roundtrip(Event e) throws IOException {
        final byte[] body = serializer.writeEvent(e);
        final Event o;

        try (final InputStream input = new ByteArrayInputStream(body)) {
            o = serializer.readEvent(input);
        }

        assertEquals(e, o);
    }

    @Test
    public void testEventFields() throws IOException {
        final Event e = Event.empty();

        roundtrip(e);
        roundtrip(e.attribute("foo", "bar"));
        roundtrip(e.time(2));
        roundtrip(e.key("foo"));
        roundtrip(e.value(0.1d));
        roundtrip(e.host("bar"));
        roundtrip(e.key("bar"));
        roundtrip(e.state("bar"));
        roundtrip(e.description("bar"));
        roundtrip(e.ttl(1));
        roundtrip(e.tag("bar"));
        roundtrip(e.attribute("bar", "foo"));
    }

    @Test
    public void testMetricFields() throws IOException {
        final Metric e = Metric.empty();

        roundtrip(e);
        roundtrip(e.proc("bar"));
        roundtrip(e.attribute("foo", "bar"));
        roundtrip(e.time(2));
        roundtrip(e.key("foo"));
        roundtrip(e.value(0.1d));
        roundtrip(e.host("bar"));
        roundtrip(e.key("bar"));
        roundtrip(e.tag("bar"));
        roundtrip(e.attribute("bar", "foo"));
    }

    @Test
    public void testPackBatch() throws IOException {
        final List<Event> events = new ArrayList<>();
        final List<Metric> metrics = new ArrayList<>();

        metrics.add(Metric.empty().key("foo"));

        final Batch batch = new Batch(new HashMap<String, String>(), events, metrics);

        final byte[] bytes;

        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            serializer.streamBatch(batch, BatchOption.COMPRESS_GZIP).stream(output);
            bytes = output.toByteArray();
        }

        final Batch b;

        try (final InputStream input = new ByteArrayInputStream(bytes)) {
            b = serializer.readBatch(input);
        }

        assertEquals(batch, b);
    }
}