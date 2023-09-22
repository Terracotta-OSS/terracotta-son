/*
 * Copyright (c) 2020-2023 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.terracottatech.tcson;

import com.terracottatech.tcson.writing.SonStreamingListWriter;
import com.terracottatech.tcson.writing.SonStreamingMapWriter;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Ultimately, this tests everything in the core Pile/Son path.
 * It's not as unit-y as you might like, but it is really thorough.
 * I thought about going more thorough, with random generation,
 * but that struck me as overkill.
 */
public class SonRoundTripTest {

  @Test
  public void testListMapList() {
    SonStreamingListWriter<Void> w = Son.streamingListWriter();
    w.append(true);
    w.append((short) 12);
    w.append(12);
    w.append((long) 12);
    w.append(13.0f);
    w.append(13.0d);
    w.append('&');
    SonStreamingMapWriter<SonStreamingListWriter<Void>> w2 = w.map();
    w2.append("one", "two");
    w2.append("two", 2);
    SonStreamingListWriter<SonStreamingMapWriter<SonStreamingListWriter<Void>>> w3 = w2.list("list");
    w3.append(10);
    w3.append(12);
    w3.append("foo");
    w3.endList();
    w2.endMap();
    w.append("some string");
    w.append((byte) 10, new byte[] { 1, 7, 4, 5 });
    w.append(UTCMillisDate.now());
    w.appendNull();
    w.append(UUID.randomUUID());
    w.endList();
    ByteBuffer b = w.buffer().getBuffer();
    b.flip();

    ReadableSonList start = Son.readableList(b);
    roundTripList(start);
  }

  @Test
  public void testMapListMap() {
    SonStreamingMapWriter<Void> w = Son.streamingMapWriter();
    w.append("key1", true);
    w.append("key2", (short) 12);
    w.append("key3", 12);
    w.append("key4", (long) 12);
    w.append("key5", 13.0f);
    SonStreamingListWriter<SonStreamingMapWriter<Void>> w1 = w.list("list");
    w1.append(10);
    w1.append(true);
    SonStreamingMapWriter<SonStreamingListWriter<SonStreamingMapWriter<Void>>> w2 = w1.map();
    w2.appendNull("noval");
    w2.append("key2", 10);
    w2.endMap();
    w1.endList();
    w.append("key6", 13.0d);
    w.append("key7", '&');
    w.append("key8", "some string");
    w.append("key9", (byte) 10, new byte[] { 1, 7, 4, 5 });
    w.append("key10", UTCMillisDate.now());
    w.appendNull("key11");
    w.append("key12", UUID.randomUUID());
    w.endMap();
    ByteBuffer b = w.buffer().getBuffer();
    b.flip();

    ReadableSonMap start = Son.readableMap(b);
    roundTripMap(start);
  }

  @Test
  public void testSimpleList() {
    SonStreamingListWriter<Void> w = Son.streamingListWriter();
    w.append(true);
    w.append((short) 12);
    w.append(12);
    w.append((long) 12);
    w.append(13.0f);
    w.append(13.0d);
    w.append('&');
    w.append('\n');
    w.append('\r');
    w.append('\t');
    w.append('\'');
    w.append('\\');
    w.append("some string");
    w.append((byte) 10, new byte[] { 1, 7, 4, 5 });
    w.append(UTCMillisDate.now());
    w.appendNull();
    w.append(UUID.randomUUID());
    w.endList();
    ByteBuffer b = w.buffer().getBuffer();
    b.flip();

    ReadableSonList start = Son.readableList(b);
    roundTripList(start);
  }

  private void roundTripList(SonList<?> start) {
    //System.out.println(start);
    MutableSonList mutable = start.asMutable();
    ByteBuffer buf = mutable.toBuffer();
    ReadableSonList ro = Son.readableList(buf);
    assertTrue(start.deepEquals(mutable));
    assertTrue(start.deepEquals(ro));
    assertTrue(mutable.deepEquals(ro));
    assertTrue(mutable.deepEquals(start));
    assertTrue(ro.deepEquals(start));
    assertTrue(ro.deepEquals(mutable));
  }

  @Test
  public void testSimpleMap() {
    SonStreamingMapWriter<Void> w = Son.streamingMapWriter();
    w.append("key1", true);
    w.append("key2", (short) 12);
    w.append("key3", 12);
    w.append("key4", (long) 12);
    w.append("key5", 13.0f);
    w.append("key6", 13.0d);
    w.append("key7", '&');
    w.append("keyy7", '\\');
    w.append("keyy8", '\'');
    w.append("key8", "some string");
    w.append("key9", (byte) 10, new byte[] { 1, 7, 4, 5 });
    w.append("key10", UTCMillisDate.now());
    w.appendNull("key11");
    w.append("key12", UUID.randomUUID());
    w.endMap();
    ByteBuffer b = w.buffer().getBuffer();
    b.flip();

    ReadableSonMap start = Son.readableMap(b);
    roundTripMap(start);
  }

  private void roundTripMap(SonMap<?> start) {
    //System.out.println(start);
    MutableSonMap mutable = start.asMutable();
    ByteBuffer buf = mutable.toBuffer();
    ReadableSonMap ro = Son.readableMap(buf);
    assertTrue(start.deepEquals(mutable));
    assertTrue(start.deepEquals(ro));
    assertTrue(mutable.deepEquals(ro));
    assertTrue(mutable.deepEquals(start));
    assertTrue(ro.deepEquals(start));
    assertTrue(ro.deepEquals(mutable));
  }
}
