package eu.toolchain.ffwd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UDPTransport implements Transport {
    final DatagramSocket socket;
    final InetAddress addr;
    final int port;

    public UDPTransport(DatagramSocket socket, InetAddress addr, int port) {
        this.socket = socket;
        this.addr = addr;
        this.port = port;
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

        final DatagramPacket packet = new DatagramPacket(send, send.length,
                addr, port);

        socket.send(packet);
    }
}