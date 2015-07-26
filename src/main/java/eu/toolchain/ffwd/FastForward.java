package eu.toolchain.ffwd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class FastForward {
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 19091;

    public static final byte BATCH = 0x1;
    public static final byte METRIC = 0x2;
    public static final byte EVENT = 0x3;

    final int version;
    final Transport transport;
    final VersionSerializer serializer;

    private FastForward(int version, Transport transport, VersionSerializer serializer) {
        this.version = version;
        this.transport = transport;
        this.serializer = serializer;
    }

    public void send(Batch batch, BatchOption... options) throws IOException {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            serializer.streamBatch(batch, options).stream(output);
            transport.send(version, BATCH, output.toByteArray());
        }
    }

    public void send(Metric metric) throws IOException {
        transport.send(version, METRIC, serializer.writeMetric(metric));
    }

    public void send(Event event) throws IOException {
        transport.send(version, EVENT, serializer.writeEvent(event));
    }

    public static FastForward setup() throws IOException {
        return setup(InetAddress.getByName(DEFAULT_HOST), DEFAULT_PORT);
    }

    public static FastForward setup(String host) throws IOException {
        return setup(InetAddress.getByName(host));
    }

    public static FastForward setup(String host, int port) throws IOException {
        return setup(InetAddress.getByName(host), port);
    }

    public static FastForward setup(InetAddress addr) throws IOException {
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
        final UDPTransport transport = new UDPTransport(socket, addr, port);
        final VersionSerializer serializer = new VersionSerializer0();
        return new FastForward(0, transport, serializer);
    }

    public static Metric metric(String key) {
        return Metric.empty().key(key);
    }

    public static Event event(String key) {
        return new Event().key(key);
    }
}
