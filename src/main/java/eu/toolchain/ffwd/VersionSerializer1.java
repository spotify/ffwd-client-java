package eu.toolchain.ffwd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

public class VersionSerializer1 implements VersionSerializer {
    final MessagePack pack = new MessagePack();

    @Override
    public VersionStreamer streamMetric(final Metric m) {
        return new VersionStreamer() {
            @Override
            public void stream(OutputStream output) throws IOException {
                final Packer packer = pack.createPacker(output);
                packMetric(packer, m);
            }
        };
    }

    @Override
    public byte[] writeMetric(Metric m) throws IOException {
        final BufferPacker packer = pack.createBufferPacker();
        packMetric(packer, m);
        return packer.toByteArray();
    }

    @Override
    public Metric readMetric(InputStream input) throws IOException {
        return unpackMetric(pack.createUnpacker(input));
    }

    @Override
    public VersionStreamer streamEvent(final Event e) {
        return new VersionStreamer() {
            @Override
            public void stream(OutputStream output) throws IOException {
                packEvent(pack.createPacker(output), e);
            }
        };
    }

    @Override
    public byte[] writeEvent(Event e) throws IOException {
        try (final BufferPacker packer = pack.createBufferPacker()) {
            packEvent(packer, e);
            return packer.toByteArray();
        }
    }

    @Override
    public Event readEvent(InputStream input) throws IOException {
        return unpackEvent(pack.createUnpacker(input));
    }

    @Override
    public VersionStreamer streamBatch(final Batch batch, final BatchOption... options) {
        final BatchHeader header = convertOptions(options);

        return new VersionStreamer() {
            @Override
            public void stream(OutputStream output) throws IOException {
                final Packer packer = pack.createPacker(output);

                packHeader(packer, header);

                if (header.gzip) {
                    try (final GZIPOutputStream out = new GZIPOutputStream(output)) {
                        final Packer gzipPacker = pack.createPacker(out);
                        packBatch(gzipPacker, batch);
                    }

                    return;
                }

                packBatch(packer, batch);
            }
        };
    }

    @Override
    public Batch readBatch(InputStream input) throws IOException {
        final Unpacker unpacker = pack.createUnpacker(input);

        final BatchHeader header = unpackHeader(unpacker);

        if (header.gzip) {
            return unpackBatch(pack.createUnpacker(new GZIPInputStream(input)));
        }

        return unpackBatch(unpacker);
    }

    static Metric unpackMetric(Unpacker unpacker) throws IOException {
        Metric m = Metric.empty();

        final long has = unpacker.readLong();

        if ((Metric.PROC & has) != 0) {
            m = m.proc(unpacker.readString());
        }

        if ((Metric.TIME & has) != 0) {
            m = m.time(unpacker.readLong());
        }

        if ((Metric.KEY & has) != 0) {
            m = m.key(unpacker.readString());
        }

        if ((Metric.VALUE & has) != 0) {
            m = m.value(unpacker.readDouble());
        }

        if ((Metric.HOST & has) != 0) {
            m = m.host(unpacker.readString());
        }

        if ((Metric.TAGS & has) != 0) {
            m = m.tags(unpackTags(unpacker));
        }

        if ((Metric.ATTRIBUTES & has) != 0) {
            m = m.attributes(unpackAttributes(unpacker));
        }

        return m;
    }

    static void packMetric(Packer packer, Metric m) throws IOException {
        packer.write(m.has);

        if (m.test(Metric.PROC)) {
            packer.write(m.proc);
        }

        if (m.test(Metric.TIME)) {
            packer.write(m.time);
        }

        if (m.test(Metric.KEY)) {
            packer.write(m.key);
        }

        if (m.test(Metric.VALUE)) {
            packer.write(m.value);
        }

        if (m.test(Metric.HOST)) {
            packer.write(m.host);
        }

        if (m.test(Metric.TAGS)) {
            packTags(packer, m.tags);
        }

        if (m.test(Metric.ATTRIBUTES)) {
            packAttributes(packer, m.attributes);
        }
    }

    static Event unpackEvent(Unpacker unpacker) throws IOException {
        Event e = Event.empty();

        final long has = unpacker.readLong();

        if ((Event.TIME & has) != 0) {
            e = e.time(unpacker.readLong());
        }

        if ((Event.KEY & has) != 0) {
            e = e.key(unpacker.readString());
        }

        if ((Event.VALUE & has) != 0) {
            e = e.value(unpacker.readDouble());
        }

        if ((Event.HOST & has) != 0) {
            e = e.host(unpacker.readString());
        }

        if ((Event.STATE & has) != 0) {
            e = e.state(unpacker.readString());
        }

        if ((Event.DESCRIPTION & has) != 0) {
            e = e.description(unpacker.readString());
        }

        if ((Event.TTL & has) != 0) {
            e = e.ttl(unpacker.readLong());
        }

        if ((Event.TAGS & has) != 0) {
            e = e.tags(unpackTags(unpacker));
        }

        if ((Event.ATTRIBUTES & has) != 0) {
            e = e.attributes(unpackAttributes(unpacker));
        }

        return e;
    }

    static void packEvent(Packer packer, Event e) throws IOException {
        packer.write(e.has);

        if (e.test(Event.TIME)) {
            packer.write(e.time);
        }

        if (e.test(Event.KEY)) {
            packer.write(e.key);
        }

        if (e.test(Event.VALUE)) {
            packer.write(e.value);
        }

        if (e.test(Event.HOST)) {
            packer.write(e.host);
        }

        if (e.test(Event.STATE)) {
            packer.write(e.state);
        }

        if (e.test(Event.DESCRIPTION)) {
            packer.write(e.description);
        }

        if (e.test(Event.TTL)) {
            packer.write(e.ttl);
        }

        if (e.test(Event.TAGS)) {
            packTags(packer, e.tags);
        }

        if (e.test(Event.ATTRIBUTES)) {
            packAttributes(packer, e.attributes);
        }
    }

    static Batch unpackBatch(Unpacker unpacker) throws IOException {
        final Map<String, String> attributes = unpackAttributes(unpacker);

        final List<Event> events;
        final List<Metric> metrics;

        {
            final int size = unpacker.readInt();
            events = new ArrayList<>(size);

            int i = 0;

            while (i++ < size) {
                events.add(unpackEvent(unpacker));
            }
        }

        {
            final int size = unpacker.readInt();
            metrics = new ArrayList<>(size);

            int i = 0;

            while (i++ < size) {
                metrics.add(unpackMetric(unpacker));
            }
        }

        return new Batch(attributes, events, metrics);
    }

    static void packBatch(Packer packer, Batch batch) throws IOException {
        packAttributes(packer, batch.attributes);

        packer.write(batch.events.size());

        for (final Event e : batch.events) {
            packEvent(packer, e);
        }

        packer.write(batch.metrics.size());

        for (final Metric m : batch.metrics) {
            packMetric(packer, m);
        }
    }

    static BatchHeader convertOptions(BatchOption[] options) {
        boolean gzip = false;

        for (final BatchOption option : options) {
            if (option == BatchOption.COMPRESS_GZIP) {
                gzip = true;
            }
        }

        return new BatchHeader(gzip);
    }

    static void packHeader(Packer packer, BatchHeader header) throws IOException {
        // number of options
        packer.writeMapBegin(1);

        packer.write("gzip");
        packer.write(header.gzip);

        packer.writeMapEnd();
    }

    static BatchHeader unpackHeader(Unpacker unpacker) throws IOException {
        final int size = unpacker.readMapBegin();

        boolean gzip = false;

        int i = 0;

        while (i++ < size) {
            final String name = unpacker.readString();

            if ("gzip".equals(name)) {
                gzip = unpacker.readBoolean();
                continue;
            }

            unpacker.skip();
        }

        unpacker.readMapEnd();
        return new BatchHeader(gzip);
    }

    static List<String> unpackTags(Unpacker unpacker) throws IOException {
        final int size = unpacker.readArrayBegin();

        final List<String> tags = new ArrayList<>(size);

        int i = 0;

        while (i++ < size) {
            tags.add(unpacker.readString());
        }

        unpacker.readArrayEnd();
        return tags;
    }

    static Map<String, String> unpackAttributes(Unpacker unpacker) throws IOException {
        final int size = unpacker.readMapBegin();

        final Map<String, String> attributes = new HashMap<>(size);

        int i = 0;

        while (i++ < size) {
            final String key = unpacker.readString();
            final String value = unpacker.readString();
            attributes.put(key, value);
        }

        unpacker.readMapEnd();
        return attributes;
    }

    static void packTags(Packer packer, final List<String> tags) throws IOException {
        packer.writeArrayBegin(tags.size());

        for (final String tag : tags)
            packer.write(tag);

        packer.writeArrayEnd();
    }

    static void packAttributes(Packer packer, final Map<String, String> attributes) throws IOException {
        final Set<Entry<String, String>> entries = attributes.entrySet();

        packer.writeMapBegin(entries.size());

        for (final Map.Entry<String, String> entry : entries) {
            packer.write(entry.getKey());
            packer.write(entry.getValue());
        }

        packer.writeMapEnd();
    }

    static class BatchHeader {
        final boolean gzip;

        public BatchHeader(boolean gzip) {
            this.gzip = gzip;
        }
    }
}