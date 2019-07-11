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

package com.spotify.ffwd.examples;

import com.spotify.ffwd.Event;
import com.spotify.ffwd.FastForward;
import java.io.IOException;

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