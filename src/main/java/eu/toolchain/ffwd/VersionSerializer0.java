package eu.toolchain.ffwd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.spotify.ffwd.protocol0.Protocol0;
import com.spotify.ffwd.protocol0.Protocol0.Attribute;

public class VersionSerializer0 implements VersionSerializer {
    @Override
    public byte[] writeMetric(Metric m) {
        final Protocol0.Metric.Builder builder = Protocol0.Metric.newBuilder();

        if (m.test(Metric.PROC))
            builder.setProc(m.proc);

        if (m.test(Metric.TIME))
            builder.setTime(m.time);

        if (m.test(Metric.KEY))
            builder.setKey(m.key);

        if (m.test(Metric.VALUE))
            builder.setValue(m.value);

        if (m.test(Metric.HOST))
            builder.setHost(m.host);

        if (m.test(Metric.TAGS)) {
            for (final String tag : m.tags) {
                builder.addTags(tag);
            }
        }

        if (m.test(Metric.ATTRIBUTES)) {
            for (final Map.Entry<String, String> entry : m.attributes.entrySet()) {
                if (entry.getKey() == null)
                    continue;

                final Attribute.Builder attributeBuilder = Protocol0.Attribute.newBuilder().setKey(entry.getKey());

                if (entry.getValue() != null)
                    attributeBuilder.setValue(entry.getValue());

                builder.addAttributes(attributeBuilder.build());
            }
        }

        final Protocol0.Metric metric = builder.build();
        return Protocol0.Message.newBuilder().setMetric(metric).build().toByteArray();
    }

    @Override
    public byte[] writeEvent(Event e) {
        final Protocol0.Event.Builder builder = Protocol0.Event.newBuilder();

        if (e.test(Event.TIME))
            builder.setTime(e.getTime());

        if (e.test(Event.KEY))
            builder.setKey(e.getKey());

        if (e.test(Event.VALUE))
            builder.setValue(e.getValue());

        if (e.test(Event.HOST))
            builder.setHost(e.getHost());

        if (e.test(Event.STATE))
            builder.setState(e.getState());

        if (e.test(Event.DESCRIPTION))
            builder.setDescription(e.getDescription());

        if (e.test(Event.TTL))
            builder.setTtl(e.getTtl());

        if (e.test(Event.TAGS)) {
            for (final String tag : e.getTags()) {
                builder.addTags(tag);
            }
        }

        if (e.test(Event.ATTRIBUTES)) {
            for (final Map.Entry<String, String> entry : e.getAttributes().entrySet()) {
                if (entry.getKey() == null)
                    continue;

                final Attribute.Builder attributeBuilder = Protocol0.Attribute.newBuilder().setKey(entry.getKey());

                if (entry.getValue() != null)
                    attributeBuilder.setValue(entry.getValue());

                builder.addAttributes(attributeBuilder.build());
            }
        }

        final Protocol0.Event event = builder.build();
        return Protocol0.Message.newBuilder().setEvent(event).build().toByteArray();
    }

    @Override
    public VersionStreamer streamBatch(Batch m, BatchOption... options) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Batch readBatch(InputStream input) throws IOException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public VersionStreamer streamMetric(Metric m) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Metric readMetric(InputStream input) throws IOException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public VersionStreamer streamEvent(Event e) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Event readEvent(InputStream input) throws IOException {
        throw new IllegalStateException("not implemented");
    }
}