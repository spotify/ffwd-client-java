package eu.toolchain.ffwd;

import java.io.IOException;
import java.io.OutputStream;

public interface Transport {
    void send(int version, byte type, byte[] bytes) throws IOException;
}