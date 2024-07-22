/*
 * Copyright Super iPaaS Integration LLC, an IBM Company 2020, 2024
 *
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