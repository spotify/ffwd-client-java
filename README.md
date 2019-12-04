# A java client to FastForward

[![CircleCI](https://circleci.com/gh/spotify/ffwd-client-java.svg?style=svg)](https://circleci.com/gh/spotify/ffwd-client-java)

A java client for the native protobuf protocol of [ffwd](https://github.com/spotify/ffwd).

## Usage

```java
public class Foo {
    private static final FastForward ffwd = FastForward.setup();
    private static final Metric metric = FastForward.metric("foo.metric").attribute("class", Foo.class.getCanonicalName());

    public void run() throws IOException {
        ffwd.send(metric.value(42));
    }
}
```

## Installation

ffwd-java-client is built and distributed through maven.

## Contributing

1. Fork ffwd-java-client from [GitHub](https://github.com/spotify/ffwd-java-client) and clone your fork.
2. Hack.
3. Push the branch back to GitHub.
4. Send a pull request to our upstream repo.

## Releasing

```sh
#> mvn versions:set -DnewVersion=<version>
#> mvn clean deploy -P release
```
