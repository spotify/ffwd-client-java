/*-
 * -\-\-
 * FastForward Test
 * --
 * Copyright (C) 2016 - 2020 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.ffwd;

import com.spotify.ffwd.protocol0.Protocol0;
import com.spotify.ffwd.protocol0.Protocol0.Attribute;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class FastForwardTest {

  @Test
  public void testDefaultFfwdClient() throws Exception {
    final Metric outboundMetric = new Metric()
        .attribute("foo", "bar")
        .host("someHost")
        .key("someKey")
        .time(System.currentTimeMillis())
        .proc("someProc")
        .tag("someTag")
        .value(1.5);

    final FastForward fastForwardClient = FastForward.setup();
    final FastForwardSocket testSocket = new FastForwardSocket(FastForward.DEFAULT_PORT);

    testSocket.start();
    fastForwardClient.send(outboundMetric);

    // Submitting to FastForward UDP socket doesn't happen synchronously in FastForwardClient,
    // allow a slight delay
    final ExecutorService executorService = Executors.newFixedThreadPool(1);
    final List<com.spotify.ffwd.protocol0.Protocol0.Metric> receivedMetrics =
        executorService.submit(testSocket.awaitMetrics()).get(5, TimeUnit.SECONDS);

    testSocket.shutDown();
    testSocket.shutDown();
    executorService.shutdown();

    Assert.assertEquals(1, receivedMetrics.size());
    final com.spotify.ffwd.protocol0.Protocol0.Metric inboundMetric = receivedMetrics.get(0);

    Assert.assertEquals(outboundMetric.getValue(), inboundMetric.getValue(), 0.0);
    Assert.assertEquals(outboundMetric.getTime(), inboundMetric.getTime());
    Assert.assertEquals(outboundMetric.getKey(), inboundMetric.getKey());
    Assert.assertEquals(outboundMetric.getHost(), inboundMetric.getHost());
    Assert.assertEquals(outboundMetric.getProc(), inboundMetric.getProc());
    Assert.assertEquals(outboundMetric.getTags(), inboundMetric.getTagsList());
    Assert.assertEquals(
        outboundMetric.getAttributes(),
        inboundMetric.getAttributesList()
            .stream()
            .collect(Collectors.toMap(Attribute::getKey, Attribute::getValue)));
  }

  // A mock implementation of a FastForward UDP socket that buffers received metrics in memory.
  private static class FastForwardSocket extends Thread {
    private static byte[] maxFrameSize = new byte[0xffffff];

    private final List<com.spotify.ffwd.protocol0.Protocol0.Metric> receivedMetrics;
    private final DatagramSocket socket;
    private boolean running;

    FastForwardSocket(int port) throws SocketException {
      this.socket = new DatagramSocket(port);
      this.receivedMetrics = new ArrayList<>();
    }

    void shutDown() {
      this.running = false;
    }

    @Override
    public synchronized void start() {
      super.start();
      this.running = true;
    }

    @Override
    public void run() {
      while (running) {
        final DatagramPacket packet = new DatagramPacket(maxFrameSize, maxFrameSize.length);
        try {
          socket.setSoTimeout(5000);
          socket.receive(packet);
        } catch (IOException e) {
          running = false;
          break;
        }
        final ByteBuffer packetBytes = ByteBuffer.wrap(packet.getData());

        Assert.assertEquals(FastForward.LATEST_VERSION, packetBytes.getInt());

        int bufferCapacity = packetBytes.getInt();
        byte[] payload = new byte[bufferCapacity - 8];
        packetBytes.get(payload, 0, bufferCapacity - 8);

        try {
          this.receivedMetrics.add(com.spotify.ffwd.protocol0.Protocol0.Message.parseFrom(payload).getMetric());
        } catch (Exception e) {
          Assert.fail("Received a malformed Protobuf message from FastForward: " + e);
          break;
        }
      }
      socket.close();
    }

    // An awaitable task that waits for metrics to be received by the socket.
    Callable<List<Protocol0.Metric>> awaitMetrics() {
      return () -> {
        while (receivedMetrics.isEmpty()) {
          Thread.sleep(100);
        }
        return receivedMetrics;
      };
    }
  }
}
