/*
 * Copyright (c) 2011-2023 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson;

import com.terracottatech.tcson.mutable.MutableSonListBuilder;
import com.terracottatech.tcson.mutable.MutableSonMapBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import static org.hamcrest.Matchers.is;

public class MutableSonTest {

  @Test
  public void testMapAndArray() {
    MutableSonMapBuilder writer = Son.writeableMap().builder();
    writer.put("int", 10);
    writer.put("foo", "happy");
    writer.put("blah", 'l');
    writer.put("fl", 32.5f);
    writer.put("lst", Son.writeableList()
                         .builder()
                         .add(10)
                         .add(11)
                         .add("hi!")
                         .addNull()
                         .add(Son.writeableMap()
                                 .builder()
                                 .put("chris", true)
                                 .putNull("f")
                                 .put("bob", "na ra")
                                 .get())
                         .add(32.5f)
                         .get());
    writer.put("barr", new SonBytes((byte) 1, new byte[] { 1, 2, 4, 8 }, 0, 4));
    writer.put("barr-none", new SonBytes((byte) 1, new byte[0], 0, 0));
    MutableSonMap m = writer.get();

    Assert.assertThat(m.get("int").intValue(), is(10));
    Assert.assertThat(m.get("foo").stringValue(), is("happy"));
    Assert.assertThat(m.get("fl").floatValue(), is(32.5f));
    Assert.assertThat(m.get("barr").bytesValue().getSignifier(), is((byte) 1));
    Assert.assertThat(m.get("barr").bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[] { 1, 2, 4, 8 })));
    Assert.assertThat(m.get("barr-none").bytesValue().getSignifier(), is((byte) 1));
    Assert.assertThat(m.get("barr-none").bytesValue().getBuffer(), is(ByteBuffer.wrap(new byte[0])));
    SonList<?> l = m.get("lst").listValue();
    Assert.assertThat(l.get(0).intValue(), is(10));
    Assert.assertThat(l.get(1).intValue(), is(11));
    Assert.assertThat(l.get(2).stringValue(), is("hi!"));
    Assert.assertThat(l.get(3).isNullValue(), is(true));
    SonMap<?> im = l.get(4).mapValue();

    Assert.assertThat(im.get("f").isNullValue(), is(true));
    Assert.assertThat(im.get("bob").stringValue(), is("na ra"));
    Assert.assertThat(im.get("chris").boolValue(), is(true));
    Assert.assertThat(l.get(5).floatValue(), is(32.5f));

  }

  @Test
  public void testSimpleList() throws IOException {
    UTCMillisDate dt = UTCMillisDate.now();
    UUID uuid = UUID.randomUUID();
    byte[] barr = new byte[] { 1, 7, 4, 5 };

    MutableSonListBuilder writer = Son.writeableList().builder();
    writer.add(true);
    writer.add((short) 12);
    writer.add(12);
    writer.add((long) 12);
    writer.add(13.0f);
    writer.add(13.0d);
    writer.add('&');
    writer.add("some string");
    writer.add(new SonBytes((byte) 10, barr));
    writer.add(dt);
    writer.addNull();
    writer.add(uuid);
    writer.add(new SonBytes((byte) 10, new byte[0]));

    MutableSonList m = writer.get();

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
    Assert.assertThat(m.get(12).bytesValue().getSignifier(), is((byte) 10));
  }

  @Test
  public void testSimpleMap() throws IOException {
    UTCMillisDate dt = UTCMillisDate.now();
    UUID uuid = UUID.randomUUID();
    byte[] barr = new byte[] { 1, 7, 4, 5 };

    MutableSonMapBuilder writer = Son.writeableMap().builder();
    writer.put("key1", true);
    writer.put("k2", (short) 12);
    writer.put("ffkey3", 12);
    writer.put("keloy4", (long) 12);
    writer.put("kedsfy5", 13.0f);
    writer.put("keddy6", 13.0d);
    writer.put("key7hh", '&');
    writer.put("key84", "some string");
    writer.put("key9gg", new SonBytes((byte) 10, barr));
    writer.put("key9hh", new SonBytes((byte) 10, new byte[0]));
    writer.put("key10o", dt);
    writer.putNull("kelly11");
    writer.put("kerry12", uuid);
    MutableSonMap m = writer.get();

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
