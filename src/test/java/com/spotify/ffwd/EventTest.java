package com.spotify.ffwd;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.toolchain.ffwd.Event;

public class EventTest {
    @Test
    public void testEquals() {
        Event e = Event.empty();

        assertEquals(e, e);
        assertEquals(e.key("foo"), e.key("foo"));

        assertNotEquals(e.time(2), e.time(1));
        assertNotEquals(e.key("foo"), e.key("bar"));
        assertNotEquals(e.value(0.2d), e.value(0.1d));
        assertNotEquals(e.host("bar"), e.host("foo"));
        assertNotEquals(e.key("bar"), e.key("foo"));
        assertNotEquals(e.state("bar"), e.state("foo"));
        assertNotEquals(e.description("bar"), e.description("foo"));
        assertNotEquals(e.ttl(1), e.ttl(2));
        assertNotEquals(e.tag("bar"), e.tag("foo"));
        assertNotEquals(e.attribute("bar", "foo"), e.attribute("foo", "bar"));

        assertEquals(e.attribute("foo", "bar"), e.attribute("foo", "bar"));
    }
}