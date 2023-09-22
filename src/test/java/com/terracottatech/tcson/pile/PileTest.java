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
package com.terracottatech.tcson.pile;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.hamcrest.Matchers.is;

public class PileTest {

  @Test
  public void test10InASharedBuffer() {
    // test you can write multiple in a buffer, then retrieve them
    ManagedBuffer dest = new ManagedBuffer(64);
    int[] lengths = new int[10];
    for (int i = 0; i < lengths.length; i++) {
      int pos = dest.getBuffer().position();
      PileWriterImpl pw = new PileWriterImpl(Pile.Type.PILE1, dest);
      pw.bool(true);
      pw.chr('d');
      pw.str("is fun");
      pw.zigzag32(1 << (i * 2));
      pw.int16((short) i);
      pw.endPile();
      int len = dest.getBuffer().position() - pos;
      lengths[i] = len;
    }
    ByteBuffer b = dest.getBuffer();
    b.flip();
    // assert we grew
    Assert.assertThat(b.remaining(), Matchers.greaterThan(64));
    int pos = 0;
    for (int i = 0; i < lengths.length; i++) {
      int lim = pos + lengths[i];
      PileReaderImpl pr = new PileReaderImpl(b, pos, lim);
      Assert.assertThat(pr.int16(4), is((short) i));
      pos = lim;
    }
  }

  @Test
  public void testEmpty() {
    PileWriterImpl pw = new PileWriterImpl(Pile.Type.PILE1, 1024);
    pw.endPile();
    ByteBuffer buf = pw.managedBuffer().getBuffer();
    buf.flip();
    PileReaderImpl pr = new PileReaderImpl(buf);
    Assert.assertThat(pr.size(), is(0));
  }

  @Test
  public void testEvery() {
    byte[] barr = new byte[] { 1, 7, 3, 4, 22, 54 };

    PileWriterImpl pw = new PileWriterImpl(Pile.Type.PILE1, 1024);

    pw.zigzag32(111);
    pw.zigzag64(-10028478923L);
    pw.str("happy string");
    pw.int8((byte) 1);
    pw.int16((short) 1);
    pw.int32(345);
    pw.int64(345);
    pw.float32(34.5f);
    pw.float64(3.45d);
    pw.bool(true);
    pw.chr('j');
    pw.nullValue();
    pw.byteArray((byte) 0, ByteBuffer.wrap(barr));

    pw.endPile();

    ByteBuffer buf = pw.managedBuffer().getBuffer();
    buf.flip();
    PileReaderImpl pr = new PileReaderImpl(buf);

    Assert.assertThat(pr.size(), is(13));

    Assert.assertThat(pr.int32(0), is(111));
    Assert.assertThat(pr.int64(1), is(-10028478923L));
    Assert.assertThat(pr.str(2), is("happy string"));
    Assert.assertThat((byte) pr.int32(3), is((byte) 1));
    Assert.assertThat((short) pr.int32(4), is((short) 1));
    Assert.assertThat(pr.int32(5), is(345));
    Assert.assertThat(pr.int64(6), is(345L));
    Assert.assertThat(pr.float32(7), is(34.5f));
    Assert.assertThat(pr.float64(8), is(3.45d));
    Assert.assertThat(pr.bool(9), is(true));
    Assert.assertThat(pr.chr(10), is('j'));
    Assert.assertThat(pr.isNull(11), is(true));

    for (int i = 0; i < barr.length; i++) {
      Assert.assertThat(pr.byteArray(12).slice().get(i), is(barr[i]));
    }
  }

  @Test
  public void testNested() throws IOException {
    PileWriter pw = new PileWriterImpl(Pile.Type.PILE1, 1024);
    pw.int16((short) 1);
    pw = pw.pile(Pile.Type.PILE1).int8((byte) 1).str("test").int64(9).zigzag64(-10010101).zigzag64(20).endPile();
    pw.str("tail");
    pw.int16((short) 111);
    pw.endPile();
    ByteBuffer buf = pw.managedBuffer().getBuffer();
    buf.flip();
    PileReaderImpl pr = new PileReaderImpl(buf);
    //System.out.println(pr);
    PileReaderImpl pr2 = pr.pile(1);
    //System.out.println(pr2);
    Assert.assertThat(pr.int32(0), is(1));
    Assert.assertThat(pr.str(2), is("tail"));
    Assert.assertThat(pr.int32(3), is(111));
  }

  @Test
  public void testSimple() {
    PileWriterImpl pw = new PileWriterImpl(Pile.Type.PILE1, 1024);
    pw.int8((byte) 1);
    pw.int8((byte) 10);
    pw.int64(9);
    pw.endPile();
    ByteBuffer b = pw.managedBuffer().getBuffer();
    b.flip();
    Assert.assertThat(b.remaining(), is(14));
  }

  @Test
  public void testUnnested() {
    PileWriterImpl pw = new PileWriterImpl(Pile.Type.PILE1, 1024);
    pw.int16((short) 1);
    pw.int8((byte) 1);
    pw.str("hello");
    pw.int16((short) 111);
    pw.endPile();
    ByteBuffer buf = pw.managedBuffer().getBuffer();
    buf.flip();
    PileReaderImpl pr = new PileReaderImpl(buf);
    //System.out.println(pr);
    Assert.assertThat(pr.size(), is(4));
    Assert.assertThat(pr.int32(3), is(111));
    Assert.assertThat(pr.str(2), is("hello"));
    Assert.assertThat(pr.int32(1), is(1));
    Assert.assertThat(pr.int32(0), is(1));
  }
}
