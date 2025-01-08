/*
 * Copyright IBM Corp. 2020, 2025
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
package com.terracottatech.tcson.pile;

import java.nio.ByteBuffer;
import java.util.Objects;

public class PileWriterImpl implements PileWriter {
  private final byte[] zzArray = new byte[32];
  private final ByteBuffer zzBuffer = ByteBuffer.wrap(zzArray);
  private final Pile.Type pileType;
  private PileWriterImpl parent;
  private ManagedBuffer managedBuffer;
  private PileWriterMetaData meta;

  private PileWriterImpl(Pile.Type pileType, PileWriterImpl parent) {
    checkType(pileType);
    this.managedBuffer = parent.managedBuffer();
    this.parent = parent;
    this.pileType = pileType;
    this.meta = new PileWriterMetaData(32);
    meta.setStartPos(buffer().position());
  }

  @Override
  public ManagedBuffer managedBuffer() {
    return managedBuffer;
  }

  private ByteBuffer buffer() {
    return managedBuffer.getBuffer();
  }

  private void checkType(Pile.Type type) {
    if (!(type != null && (type == Pile.Type.PILE1 || type == Pile.Type.PILE2))) {
      throw new IllegalStateException();
    }
  }

  public PileWriterImpl(Pile.Type pileType) {
    this(pileType, 1024);
  }

  public PileWriterImpl(Pile.Type pileType, int initial) {
    checkType(pileType);
    this.pileType = pileType;
    this.managedBuffer = new ManagedBuffer(initial);
    this.meta = new PileWriterMetaData(32);
    meta.setStartPos(buffer().position());
    this.parent = null;
  }

  public PileWriterImpl(Pile.Type pileType, ManagedBuffer mbuf) {
    checkType(pileType);
    this.pileType = pileType;
    this.managedBuffer = mbuf;
    this.meta = new PileWriterMetaData(32);
    meta.setStartPos(buffer().position());
    this.parent = null;
  }

  public PileWriterImpl(Pile.Type pileType, ByteBuffer managedBuffer) {
    checkType(pileType);
    this.pileType = pileType;
    this.managedBuffer = new ManagedBuffer(managedBuffer);
    this.meta = new PileWriterMetaData(32);
    meta.setStartPos(managedBuffer.position());
    this.parent = null;
  }

  @Override
  public PileWriter bool(boolean v) {
    managedBuffer.ensureRemaining(1);
    buffer().put((byte) (v ? 1 : 0));
    meta.add(Pile.Type.BOOLEAN, 1);
    return this;
  }

  @Override
  public PileWriter byteArray(byte signifier, byte[] b, int off, int len) {
    Objects.requireNonNull(b);
    managedBuffer.ensureRemaining(len + 1);
    buffer().put(signifier);
    buffer().put(b, off, len);
    meta.add(Pile.Type.BYTE_ARRAY, len + 1);
    return this;
  }

  @Override
  public PileWriter byteArray(byte signifier, ByteBuffer buf) {
    Objects.requireNonNull(buf);
    int len = buf.remaining();
    managedBuffer.ensureRemaining(len + 1);
    buffer().put(signifier);
    buffer().put(buf);
    meta.add(Pile.Type.BYTE_ARRAY, len + 1);
    return this;
  }

  @Override
  public PileWriter chr(char c) {
    managedBuffer.ensureRemaining(2);
    buffer().putChar(c);
    meta.add(Pile.Type.CHAR, 2);
    return this;
  }

  @Override
  public PileWriter endPile() {
    writeDirectory();

    // tell parent;
    if (parent != null) {
      parent.meta.add(pileType, buffer().position() - meta.getStartPos());
    }

    // return parent;
    return parent;
  }

  private void writeDirectory() {
    // write out end
    managedBuffer.ensureRemaining(meta.maxFootprint());
    meta.writeDirectory(buffer());
  }

  @Override
  public PileWriter float32(float v) {
    managedBuffer.ensureRemaining(4);
    buffer().putInt(Float.floatToIntBits(v));
    meta.add(Pile.Type.FLOAT32, 4);
    return this;
  }

  @Override
  public PileWriter float64(double v) {
    managedBuffer.ensureRemaining(8);
    buffer().putLong(Double.doubleToLongBits(v));
    meta.add(Pile.Type.FLOAT64, 8);
    return this;
  }

  private int footprint() {
    return buffer().position() - meta.getStartPos();
  }

  @Override
  public PileWriter int16(short v) {
    managedBuffer.ensureRemaining(2);
    buffer().putShort(v);
    meta.add(Pile.Type.INT16, 2);
    return this;
  }

  @Override
  public PileWriter int32(int v) {
    managedBuffer.ensureRemaining(4);
    buffer().putInt(v);
    meta.add(Pile.Type.INT32, 4);
    return this;
  }

  @Override
  public PileWriter int64(long v) {
    managedBuffer.ensureRemaining(8);
    buffer().putLong(v);
    meta.add(Pile.Type.INT64, 8);
    return this;
  }

  @Override
  public PileWriter int8(byte v) {
    managedBuffer.ensureRemaining(1);
    buffer().put(v);
    meta.add(Pile.Type.INT8, 1);
    return this;
  }

  @Override
  public PileWriter nullValue() {
    managedBuffer.ensureRemaining(1);
    buffer().put((byte) 0);
    meta.add(Pile.Type.NULL, 1);
    return this;
  }

  @Override
  public PileWriter pile(Pile.Type pileType) {
    PileWriterImpl next = new PileWriterImpl(pileType, this);
    return next;
  }

  @Override
  public void reset() {
    managedBuffer.reset();
    meta.clear();
    meta.setStartPos(buffer().position());
  }

  @Override
  public void reset(ByteBuffer b) {
    managedBuffer.reset(b);
    this.meta = new PileWriterMetaData(32);
    meta.setStartPos(buffer().position());
    this.parent = null;
  }

  @Override
  public int size() {
    return meta.size();
  }

  @Override
  public PileWriter str(String s) {
    Objects.requireNonNull(s);
    int pos;
    int need = StringTool.worstCaseByteArraySize(s);
    managedBuffer.ensureRemaining(need);
    pos = buffer().position();
    StringTool.putEncoded(buffer(), s, s.length());
    meta.add(Pile.Type.STRING, buffer().position() - pos);
    return this;
  }

  @Override
  public PileWriter zigzag32(int v) {
    zzBuffer.clear();
    VarInts.zigzagEncode(zzBuffer, v);
    zzBuffer.flip();
    int bcnt = zzBuffer.remaining();
    managedBuffer.ensureRemaining(bcnt);
    buffer().put(zzBuffer);
    meta.add(Pile.Type.ZIGZAG32, bcnt);
    return this;
  }

  @Override
  public PileWriter zigzag64(long v) {
    zzBuffer.clear();
    VarInts.zigzagEncode(zzBuffer, v);
    zzBuffer.flip();
    int bcnt = zzBuffer.remaining();
    managedBuffer.ensureRemaining(bcnt);
    buffer().put(zzBuffer);
    meta.add(Pile.Type.ZIGZAG64, bcnt);
    return this;
  }

}
