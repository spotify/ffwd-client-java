package eu.toolchain.ffwd;

import java.io.IOException;
import java.io.OutputStream;

public interface VersionStreamer {
    public void stream(OutputStream output) throws IOException;
}