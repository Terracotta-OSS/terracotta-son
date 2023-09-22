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

import com.terracottatech.tcson.reading.ReadableSonMapImpl;
import com.terracottatech.tcson.writing.SonStreamingListWriter;
import com.terracottatech.tcson.writing.SonStreamingMapWriter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ReadableSonTest {

  @Test
  public void testKeyReuse() {
    SonStreamingMapWriter<Void> writer = Son.streamingMapWriter();
    writer.append("k1", 1);
    writer.append("k3", 3);
    SonStreamingMapWriter<SonStreamingMapWriter<Void>> w1 = writer.map("m1");
    w1.append("k3", 3);
    w1.append("k2", 2);
    w1.append("k1", 1);
    w1.endMap();
    writer.endMap();
    ByteBuffer buf = writer.buffer().getBuffer();
    buf.flip();
    ReadableSonMapImpl m = new ReadableSonMapImpl(buf);
    assertThat(m.get("k1").intValue(), is(1));
    assertThat(m.get("k3").intValue(), is(3));
    assertThat(m.get("m1").mapValue().get("k3").intValue(), is(3));
    assertThat(m.get("m1").mapValue().get("k2").intValue(), is(2));
    assertThat(m.get("m1").mapValue().get("k1").intValue(), is(1));
  }

  @Test
  public void testMapAndArray() {
    SonStreamingMapWriter<Void> writer = Son.streamingMapWriter();
    writer.append("int", 10);
    writer.append("foo", "happy");
    writer.append("blah", 'l');
    writer.append("fl", 32.5f);
    writer.list("lst")
      .append(10)
      .append(11)
      .append("hi!")
      .appendNull()
      .map()
      .append("chris", true)
      .appendNull("f")
      .append("bob", "na ra")
      .endMap()
      .append(32.5f)
      .endList();
    writer.append("barr", (byte) 1, new byte[] { 1, 2, 4, 8 }, 0, 4);
    writer.append("barr-none", (byte) 1, new byte[0], 0, 0);
    writer.endMap();

    ByteBuffer buf = writer.buffer().getBuffer();
    buf.flip();
    ReadableSonMap m = new ReadableSonMapImpl(buf);

    Assert.assertThat(m.get("int").intValue(), is(10));
    Assert.assertThat(m.get("foo").stringValue(), is("happy"));
    Assert.assertThat(m.get("fl").floatValue(), is(32.5f));
    Assert.assertThat(m.get("barr").bytesValue().getSignifier(), is((byte) 1));
    Assert.assertThat(m.get("barr").bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[] { 1, 2, 4, 8 })));
    Assert.assertThat(m.get("barr-none").bytesValue().getSignifier(), is((byte) 1));
    Assert.assertThat(m.get("barr-none").bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[0])));
    ReadableSonList l = m.get("lst").listValue();
    Assert.assertThat(l.get(0).intValue(), is(10));
    Assert.assertThat(l.get(1).intValue(), is(11));
    Assert.assertThat(l.get(2).stringValue(), is("hi!"));
    Assert.assertThat(l.get(3).isNullValue(), is(true));
    ReadableSonMap im = l.get(4).mapValue();

    Assert.assertThat(im.get("f").isNullValue(), is(true));
    Assert.assertThat(im.get("bob").stringValue(), is("na ra"));
    Assert.assertThat(im.get("chris").boolValue(), is(true));
    Assert.assertThat(l.get(5).floatValue(), is(32.5f));

  }

  @Test
  public void testMapAndArrayNameSource() {
    NameSource.Naive ns = new NameSource.Naive(100);
    SonStreamingMapWriter<Void> writer = Son.streamingMapWriter(ns);
    writer.append("int", 10);
    writer.append("foo", "happy");
    writer.append("blah", 'l');
    writer.append("fl", 32.5f);
    writer.list("lst")
      .append(10)
      .append(11)
      .append("hi!")
      .appendNull()
      .map()
      .append("chris", true)
      .append("int", 10001)
      .appendNull("f")
      .append("bob", "na ra")
      .endMap()
      .append(32.5f)
      .endList();
    writer.append("barr-none", (byte) 1, new byte[0], 0, 0);
    writer.append("barr", (byte) 1, new byte[] { 1, 2, 4, 8 }, 0, 4);
    writer.append("barr-zero", (byte) 0, new byte[0], 0, 0);
    writer.endMap();

    ByteBuffer buf = writer.buffer().getBuffer();
    buf.flip();
    SonMap<?> m = new ReadableSonMapImpl(ns, buf);

    Assert.assertThat(m.get("int").intValue(), is(10));
    Assert.assertThat(m.get("foo").stringValue(), is("happy"));
    Assert.assertThat(m.get("fl").floatValue(), is(32.5f));
    Assert.assertThat(m.get("barr").bytesValue().getSignifier(), is((byte) 1));
    Assert.assertThat(m.get("barr").bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[] { 1, 2, 4, 8 })));
    Assert.assertThat(m.get("barr-none").bytesValue().getSignifier(), is((byte) 1));
    Assert.assertThat(m.get("barr-none").bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[0])));
    Assert.assertThat(m.get("barr-zero").bytesValue().getSignifier(), is((byte) 0));
    Assert.assertThat(m.get("barr-zero").bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[0])));
    SonList<?> l = m.get("lst").listValue();
    Assert.assertThat(l.get(0).intValue(), is(10));
    Assert.assertThat(l.get(1).intValue(), is(11));
    Assert.assertThat(l.get(2).stringValue(), is("hi!"));
    Assert.assertThat(l.get(3).isNullValue(), is(true));
    SonMap<?> im = l.get(4).mapValue();

    Assert.assertThat(im.get("f").isNullValue(), is(true));
    Assert.assertThat(im.get("int").intValue(), is(10001));
    Assert.assertThat(im.get("bob").stringValue(), is("na ra"));
    Assert.assertThat(im.get("chris").boolValue(), is(true));
    Assert.assertThat(l.get(5).floatValue(), is(32.5f));

    MutableSonMap m2 = m.deepCopy();
    Assert.assertThat(m2.deepEquals(m), is(true));
    m2.get("lst").listValue().remove(1);
    Assert.assertThat(m2.deepEquals(m), is(false));

    // check old one
    Assert.assertThat(m.get("int").intValue(), is(10));
    Assert.assertThat(m.get("foo").stringValue(), is("happy"));
    Assert.assertThat(m.get("fl").floatValue(), is(32.5f));
    Assert.assertThat(m.get("barr").bytesValue().getSignifier(), is((byte) 1));
    Assert.assertThat(m.get("barr").bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[] { 1, 2, 4, 8 })));
    l = m.get("lst").listValue();
    Assert.assertThat(l.get(0).intValue(), is(10));
    Assert.assertThat(l.get(1).intValue(), is(11));
    Assert.assertThat(l.get(2).stringValue(), is("hi!"));
    Assert.assertThat(l.get(3).isNullValue(), is(true));
    im = l.get(4).mapValue();

    Assert.assertThat(im.get("f").isNullValue(), is(true));
    Assert.assertThat(im.get("int").intValue(), is(10001));
    Assert.assertThat(im.get("bob").stringValue(), is("na ra"));
    Assert.assertThat(im.get("chris").boolValue(), is(true));
    Assert.assertThat(l.get(5).floatValue(), is(32.5f));

    m = m2;
    // check old one
    Assert.assertThat(m.get("int").intValue(), is(10));
    Assert.assertThat(m.get("foo").stringValue(), is("happy"));
    Assert.assertThat(m.get("fl").floatValue(), is(32.5f));
    Assert.assertThat(m.get("barr").bytesValue().getSignifier(), is((byte) 1));
    Assert.assertThat(m.get("barr").bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[] { 1, 2, 4, 8 })));
    l = m.get("lst").listValue();
    Assert.assertThat(l.get(0).intValue(), is(10));
    Assert.assertThat(l.get(1).stringValue(), is("hi!"));
    Assert.assertThat(l.get(2).isNullValue(), is(true));
    im = l.get(3).mapValue();

    Assert.assertThat(im.get("f").isNullValue(), is(true));
    Assert.assertThat(im.get("int").intValue(), is(10001));
    Assert.assertThat(im.get("bob").stringValue(), is("na ra"));
    Assert.assertThat(im.get("chris").boolValue(), is(true));
    Assert.assertThat(l.get(4).floatValue(), is(32.5f));
  }

  @Test
  public void testSimpleList() throws IOException {
    UTCMillisDate dt = UTCMillisDate.now();
    UUID uuid = UUID.randomUUID();
    byte[] barr = new byte[] { 1, 7, 4, 5 };

    SonStreamingListWriter<Void> writer = Son.streamingListWriter();
    writer.append(true);
    writer.append((short) 12);
    writer.append(12);
    writer.append((long) 12);
    writer.append(13.0f);
    writer.append(13.0d);
    writer.append('&');
    writer.append("some string");
    writer.append((byte) 10, barr);
    writer.append(dt);
    writer.appendNull();
    writer.append(uuid);
    writer.append((byte) 0, new byte[0]);
    writer.append((byte) 10, new byte[0]);
    writer.endList();

    ByteBuffer buf = writer.buffer().getBuffer();
    buf.flip();

    ReadableSonList m = Son.readableList(buf);

    Assert.assertThat(m.get(0).boolValue(), is(true));
    Assert.assertThat(m.get(1).shortValue(), is((short) 12));
    Assert.assertThat(m.get(2).intValue(), is(12));
    Assert.assertThat(m.get(3).longValue(), is(12L));
    Assert.assertThat(m.get(4).floatValue(), is(13.0f));
    Assert.assertThat(m.get(5).doubleValue(), is(13.0d));
    Assert.assertThat(m.get(6).charValue(), is('&'));
    Assert.assertThat(m.get(7).stringValue(), is("some string"));
    Assert.assertThat(m.get(8).bytesValue().getBuffer(), is(ByteBuffer.wrap(barr)));
    Assert.assertThat(m.get(8).bytesValue().getSignifier(), is((byte) 10));
    Assert.assertThat(m.get(9).dateValue(), is(dt));
    Assert.assertThat(m.get(10).isNullValue(), is(true));
    Assert.assertThat(m.get(11).uuidValue(), is(uuid));
    Assert.assertThat(m.get(12).bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[0])));
    Assert.assertThat(m.get(13).bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[0])));
  }

  @Test
  public void testSimpleMap() throws IOException {
    UTCMillisDate dt = UTCMillisDate.now();
    UUID uuid = UUID.randomUUID();
    byte[] barr = new byte[] { 1, 7, 4, 5 };

    SonStreamingMapWriter<Void> writer = Son.streamingMapWriter();
    writer.append("key1", true);
    writer.append("k2", (short) 12);
    writer.append("ffkey3", 12);
    writer.append("keloy4", (long) 12);
    writer.append("kedsfy5", 13.0f);
    writer.append("keddy6", 13.0d);
    writer.append("key7hh", '&');
    writer.append("key84", "some string");
    writer.append("key9gg", (byte) 10, barr);
    writer.append("key9hh", (byte) 10, new byte[0]);
    writer.append("key10o", dt);
    writer.appendNull("kelly11");
    writer.append("kerry12", uuid);
    writer.endMap();

    ByteBuffer buf = writer.buffer().getBuffer();
    buf.flip();
    ReadableSonMap m = new ReadableSonMapImpl(buf);

    Assert.assertThat(m.get("key1").boolValue(), is(true));
    Assert.assertThat(m.get("kerry12").uuidValue(), is(uuid));
    Assert.assertThat(m.get("k2").shortValue(), is((short) 12));
    Assert.assertThat(m.get("kelly11").isNullValue(), is(true));
    Assert.assertThat(m.get("ffkey3").intValue(), is(12));
    Assert.assertThat(m.get("key10o").dateValue(), is(dt));
    Assert.assertThat(m.get("keloy4").longValue(), is(12L));
    Assert.assertThat(m.get("key9gg").bytesValue().getSignifier(), is((byte) 10));
    Assert.assertThat(m.get("key9gg").bytesValue().getBuffer(), is(ByteBuffer.wrap(barr)));
    Assert.assertThat(m.get("key9hh").bytesValue().getSignifier(), is((byte) 10));
    Assert.assertThat(m.get("key9hh").bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[0])));
    Assert.assertThat(m.get("kedsfy5").floatValue(), is(13.0f));
    Assert.assertThat(m.get("key84").stringValue(), is("some string"));
    Assert.assertThat(m.get("keddy6").doubleValue(), is(13.0d));
    Assert.assertThat(m.get("key7hh").charValue(), is('&'));
  }
}
