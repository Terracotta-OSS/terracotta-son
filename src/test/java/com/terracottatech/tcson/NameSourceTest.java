/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson;

import com.terracottatech.tcson.reading.ReadableSonMapImpl;
import com.terracottatech.tcson.writing.SonStreamingMapWriter;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class NameSourceTest {

  @Test
  public void testNameSource() {
    NameSource ns = new NameSource.Naive(3);
    SonStreamingMapWriter<Void> writer = Son.streamingMapWriter(ns);
    writer.append("foo", true);
    writer.append("bar", 10);
    writer.append("uu", UUID.randomUUID());
    writer.append("fuzzy", "dd");
    writer.map("map").append("happy", 10).append("a", "z").append("z", "a").endMap();
    writer.append("baz", "lur'h\"man");
    writer.append("bbb", (byte) 1, new byte[] { 9, 4, 5 });
    UTCMillisDate ts = new UTCMillisDate();
    writer.append("dt", ts);
    writer.endMap();
    ByteBuffer buf = writer.buffer().getBuffer();
    buf.flip();
    ReadableSonMap m = new ReadableSonMapImpl(ns, buf);

    assertThat(m.get("foo").boolValue(), is(true));
    assertThat(m.get("bar").intValue(), is(10));
    assertThat(m.get("baz").stringValue(), is("lur'h\"man"));
    assertThat(m.get("map").mapValue().get("happy").intValue(), is(10));
    assertThat(m.get("map2"), Matchers.nullValue());
    assertThat(m.get("map").mapValue().get("a").stringValue(), Matchers.is("z"));
    assertThat(m.get("dt").dateValue(), is(ts));

  }
}