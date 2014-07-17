package eu.toolchain.ffwd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FastForward {
    public static final int LATEST_VERSION = 0;
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 19091;


    public static FastForward setup() throws UnknownHostException, SocketException {
        return setup(InetAddress.getByName(DEFAULT_HOST), DEFAULT_PORT);
    }

    public static FastForward setup(String host) throws UnknownHostException, SocketException {
        return setup(InetAddress.getByName(host));
    }

    public static FastForward setup(String host, int port) throws UnknownHostException, SocketException {
        return setup(InetAddress.getByName(host), port);
    }

    public static FastForward setup(InetAddress addr) throws SocketException {
        return setup(addr, DEFAULT_PORT);
    }

    /**
     * Initialization method for a FastForward client.
     *
     * @return A new instance of a FastForward client.
     * @throws SocketException If a datagram socket cannot be created.
     */
    public static FastForward setup(InetAddress addr, int port) throws SocketException {
        final DatagramSocket socket = new DatagramSocket();
        return new FastForward(addr, port, socket);
    }

    private final InetAddress addr;
    private final int port;
    private final DatagramSocket socket;

    private FastForward(InetAddress addr, int port, DatagramSocket socket) {
        this.addr = addr;
        this.port = port;
        this.socket = socket;
    }

    public void send(Metric metric) throws IOException {
        sendFrame(metric.serialize());
    }

    public void send(Event event) throws IOException {
        sendFrame(event.serialize());
    }

    private void sendFrame(byte[] bytes) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(bytes.length + 8);
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.putInt(LATEST_VERSION);
        buffer.putInt(buffer.capacity());
        buffer.put(bytes);
        buffer.rewind();

        final byte[] send = new byte[buffer.capacity()];
        buffer.get(send);

        final DatagramPacket packet = new DatagramPacket(send, send.length,
                addr, port);

        socket.send(packet);
    }

    public static Metric metric(String key) {
        return new Metric().key(key);
    }

    public static Event event(String key) {
        return new Event().key(key);
    }
}
