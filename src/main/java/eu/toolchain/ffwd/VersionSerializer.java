package eu.toolchain.ffwd;

import java.io.IOException;
import java.io.InputStream;

public interface VersionSerializer {
    VersionStreamer streamMetric(Metric m);
    byte[] writeMetric(Metric m) throws IOException;
    Metric readMetric(InputStream input) throws IOException;

    VersionStreamer streamEvent(Event e);
    byte[] writeEvent(Event e) throws IOException;
    Event readEvent(InputStream input) throws IOException;

    VersionStreamer streamBatch(Batch m, BatchOption... options);
    Batch readBatch(InputStream input) throws IOException;
}