package eu.toolchain.ffwd;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TCPTransport implements Transport {
    final Socket socket;

    public TCPTransport(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void send(int version, byte type, byte[] bytes) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(bytes.length + 9);
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.putInt(version);
        buffer.put(type);
        buffer.putInt(buffer.capacity());
        buffer.put(bytes);
        buffer.rewind();

        final byte[] send = new byte[buffer.capacity()];
        buffer.get(send);

        final OutputStream output = socket.getOutputStream();
        output.write(send);
        output.flush();
    }
}