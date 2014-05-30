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

    private final InetAddress addr;
    private final int port;
    private final DatagramSocket socket;

    public FastForward() {
        this.addr = setupAddress(DEFAULT_HOST);
        this.port = DEFAULT_PORT;
        this.socket = setupSocket();
    }

    public FastForward(String host) {
        this.addr = setupAddress(host);
        this.port = DEFAULT_PORT;
        this.socket = setupSocket();
    }

    public FastForward(String host, int port) {
        this.addr = setupAddress(host);
        this.port = port;
        this.socket = setupSocket();
    }

    private InetAddress setupAddress(String host) {
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    private DatagramSocket setupSocket() {
        try {
            return new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    public void send(Metric metric) {
        if (socket == null || addr == null)
            return;

        sendFrame(metric.serialize());
    }

    public void send(Event event) {
        if (socket == null || addr == null)
            return;

        sendFrame(event.serialize());
    }

    private void sendFrame(byte[] bytes) {
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

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return;
        }
    }

    public static Metric metric(String key) {
        return new Metric().key(key);
    }

    public static Event event(String key) {
        return new Event().key(key);
    }
}
