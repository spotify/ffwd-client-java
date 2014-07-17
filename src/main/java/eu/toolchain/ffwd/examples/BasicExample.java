package eu.toolchain.ffwd.examples;

import java.io.IOException;

import eu.toolchain.ffwd.Event;
import eu.toolchain.ffwd.FastForward;

public class BasicExample {
    public static void main(String[] args) throws IOException {
        FastForward client = FastForward.setup();

        // event with attribute
        client.send(FastForward.event("hello").attribute("foo", "bar"));

        // Events are immutable, so it's possible to use another event as a basis for new ones.
        final Event e = FastForward.event("hello").attribute("foo", "bar");
        client.send(e.tag("mytag"));
        client.send(e.tag("anothertag"));

        // metric with attribute, time and value.
        client.send(FastForward.metric("hello").attribute("foo", "bar").time(System.currentTimeMillis()).value(0.2));
    }
}