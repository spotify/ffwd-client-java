# A java client to FastForward &#187;

[![Build Status](https://travis-ci.org/udoprog/ffwd-java-client.svg?branch=master)](https://travis-ci.org/udoprog/ffwd-java-client)

A java client for the native protobuf protocol of
[ffwd](https://github.com/spotify/ffwd).

## Usage

```java
public class Foo {
    private static final FastForward ffwd = new FastForward();
    private static final Metric metric = FastForward.metric("foo.metric").attribute("class", Foo.class.getCanonicalName());

    public void run() {
        ffwd.send(metric.value(42));
    }
}
```

## Installation

ffwd-java-client is built and distributed through maven.

## Contributing

1. Fork ffwd-java-client from
   [github](https://github.com/udoprog/ffwd-java-client) and clone your fork.
2. Hack.
3. Push the branch back to GitHub.
4. Send a pull request to our upstream repo.
