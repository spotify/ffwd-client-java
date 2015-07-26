package com.spotify.ffwd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import eu.toolchain.ffwd.Metric;

public class MetricTest {
    @Test
    public void testEquals() {
        Metric m = Metric.empty();

        assertEquals(m, m);
        assertEquals(m.key("foo"), m.key("foo"));

        assertNotEquals(m.proc("foo"), m.proc("bar"));
        assertNotEquals(m.time(2), m.time(1));
        assertNotEquals(m.key("foo"), m.key("bar"));
        assertNotEquals(m.value(0.2d), m.value(0.1d));
        assertNotEquals(m.host("bar"), m.host("foo"));
        assertNotEquals(m.key("bar"), m.key("foo"));
        assertNotEquals(m.tag("bar"), m.tag("foo"));
        assertNotEquals(m.attribute("bar", "foo"), m.attribute("foo", "bar"));

        assertEquals(m.attribute("foo", "bar"), m.attribute("foo", "bar"));
    }
}