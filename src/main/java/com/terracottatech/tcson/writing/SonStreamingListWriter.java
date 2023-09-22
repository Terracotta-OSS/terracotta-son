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
package com.terracottatech.tcson.writing;

import com.terracottatech.tcson.NameSource;
import com.terracottatech.tcson.UTCMillisDate;
import com.terracottatech.tcson.pile.ManagedBuffer;
import com.terracottatech.tcson.pile.Pile;
import com.terracottatech.tcson.pile.PileWriter;
import com.terracottatech.tcson.pile.PileWriterImpl;

import java.nio.ByteBuffer;
import java.util.UUID;

public class SonStreamingListWriter<E> {

  private final E parent;
  private final NameSource nameSource;
  private final PileWriter writer;
  private final GlobalNameMapWriter globalNameMap;
  private byte[] tmpArray = new byte[32];
  private ByteBuffer tmpBuffer = ByteBuffer.wrap(tmpArray);

  public SonStreamingListWriter(NameSource nameSource, ManagedBuffer b) {
    this(null, nameSource, new PileWriterImpl(Pile.Type.PILE2, b), new GlobalNameMapWriter());
  }

  public SonStreamingListWriter(E parent, NameSource nameSource, PileWriter pw, GlobalNameMapWriter globalMap) {
    this.parent = parent;
    this.nameSource = nameSource;
    this.writer = pw;
    this.globalNameMap = globalMap;
  }

  public SonStreamingListWriter<E> append(boolean b) {
    writer.bool(b);
    return this;
  }

  public SonStreamingListWriter<E> append(UUID uuid) {
    tmpBuffer.clear();
    tmpBuffer.putLong(uuid.getMostSignificantBits());
    tmpBuffer.putLong(uuid.getLeastSignificantBits());
    tmpBuffer.flip();
    writer.byteArray(SonWriter.UUID_SIGNIFIER, tmpBuffer);
    return this;
  }

  public SonStreamingListWriter<E> append(UTCMillisDate b) {
    tmpBuffer.clear();
    tmpBuffer.putLong(b.utcMillis());
    tmpBuffer.flip();
    writer.byteArray(SonWriter.DATE_SIGNIFIER, tmpBuffer);
    return this;
  }

  public SonStreamingListWriter<E> append(byte b) {
    writer.int8(b);
    return this;
  }

  public SonStreamingListWriter<E> append(short b) {
    writer.int16(b);
    return this;
  }

  public SonStreamingListWriter<E> append(int b) {
    writer.zigzag32(b);
    return this;
  }

  public SonStreamingListWriter<E> append(long b) {
    writer.zigzag64(b);
    return this;
  }

  public SonStreamingListWriter<E> append(float b) {
    writer.float32(b);
    return this;
  }

  public SonStreamingListWriter<E> append(double b) {
    writer.float64(b);
    return this;
  }

  public SonStreamingListWriter<E> append(String b) {
    writer.str(b);
    return this;
  }

  public SonStreamingListWriter<E> append(char c) {
    writer.chr(c);
    return this;
  }

  public SonStreamingListWriter<E> append(byte signifier, byte[] value) {
    return append(signifier, value, 0, value.length);
  }

  public SonStreamingListWriter<E> append(byte signifier, byte[] b, int off, int len) {
    writer.byteArray(signifier, b, off, len);
    return this;
  }

  public SonStreamingListWriter<E> append(byte signifier, ByteBuffer buf) {
    writer.byteArray(signifier, buf);
    return this;
  }

  public SonStreamingListWriter<E> appendNull() {
    writer.nullValue();
    return this;
  }

  public ManagedBuffer buffer() {
    return writer.managedBuffer();
  }

  public E endList() {
    if (parent == null) {
      SonStreamingMapWriter.writeGlobalMap(nameSource, globalNameMap, writer);
    }

    writer.endPile();
    // nothing else to do, simple simple.
    return parent;
  }

  public SonStreamingListWriter<?> list() {
    PileWriter ret = writer.pile(Pile.Type.PILE2);
    return new SonStreamingListWriter<>(this, nameSource, ret, globalNameMap);
  }

  public SonStreamingMapWriter<SonStreamingListWriter<E>> map() {
    PileWriter ret = writer.pile(Pile.Type.PILE1);
    return new SonStreamingMapWriter<>(this, nameSource, ret, globalNameMap);
  }

  public void reset() {
    globalNameMap.clear();
    writer.reset();
  }

}
