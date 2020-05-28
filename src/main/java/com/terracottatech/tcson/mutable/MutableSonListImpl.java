/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.mutable;

import com.terracottatech.tcson.MutableSonList;
import com.terracottatech.tcson.MutableSonMap;
import com.terracottatech.tcson.NameSource;
import com.terracottatech.tcson.Son;
import com.terracottatech.tcson.SonBytes;
import com.terracottatech.tcson.SonList;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.SonValue;
import com.terracottatech.tcson.UTCMillisDate;
import com.terracottatech.tcson.pile.ManagedBuffer;
import com.terracottatech.tcson.writing.SonStreamingListWriter;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class MutableSonListImpl implements MutableSonList {

  private static final long serialVersionUID = -1770228071344659054L;
  private ArrayList<MutableSonValue> entries = new ArrayList<>();

  public MutableSonListImpl() {
  }

  @Override
  public synchronized MutableSonList add(int idx, SonType type, Object value) {
    entries.add(idx, new MutableSonValue(type, value));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, boolean v) {
    add(idx, new MutableSonValue(SonType.BOOL, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, MutableSonValue value) {
    Objects.requireNonNull(value);
    entries.add(idx, value);
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, UTCMillisDate v) {
    add(idx, new MutableSonValue(SonType.DATE, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, UUID v) {
    add(idx, new MutableSonValue(SonType.UUID, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, char v) {
    add(idx, new MutableSonValue(SonType.CHAR, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, String v) {
    add(idx, new MutableSonValue(SonType.STRING, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, byte v) {
    add(idx, new MutableSonValue(SonType.BYTE, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, short v) {
    add(idx, new MutableSonValue(SonType.SHORT, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, int v) {
    add(idx, new MutableSonValue(SonType.INT, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, long v) {
    add(idx, new MutableSonValue(SonType.LONG, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, float v) {
    add(idx, new MutableSonValue(SonType.FLOAT, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, double v) {
    add(idx, new MutableSonValue(SonType.DOUBLE, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, SonBytes sb) {
    add(idx, new MutableSonValue(SonType.BYTES, sb));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, MutableSonList v) {
    add(idx, new MutableSonValue(SonType.LIST, v));
    return this;
  }

  @Override
  public synchronized MutableSonList add(int idx, MutableSonMap v) {
    add(idx, new MutableSonValue(SonType.MAP, v));
    return this;
  }

  @Override
  public synchronized MutableSonList addNull(int idx) {
    add(idx, MutableSonValue.NULL_VALUE);
    return this;
  }

  @Override
  public MutableSonValue asSonValue() {
    return new MutableSonValue(SonType.LIST, this);
  }

  @Override
  public synchronized MutableSonList clear() {
    entries.clear();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof SonList) {
      return deepEquals((SonList<?>) o);
    }
    return false;
  }

  public synchronized MutableSonValue get(int idx) {
    return entries.get(idx);
  }

  @Override
  public int hashCode() {
    return deepHashCode();
  }

  @Override
  public synchronized Iterator<MutableSonValue> iterator() {
    return new ArrayList<>(entries).iterator();
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException {
    int len = in.readInt();
    byte[] dest = new byte[len];
    in.read(dest);
    MutableSonListImpl newList = (MutableSonListImpl) Son.readableList(ByteBuffer.wrap(dest)).asMutable();
    this.entries = newList.entries;
  }

  @Override
  public synchronized MutableSonList remove(int idx) {
    entries.remove(idx);
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, SonType type, Object value) {
    set(idx, new MutableSonValue(type, value));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, MutableSonValue value) {
    Objects.requireNonNull(value);
    if (idx == size()) {
      entries.add(value);
    } else {
      entries.set(idx, value);
    }
    return this;
  }

  public synchronized int size() {
    return entries.size();
  }

  @Override
  public synchronized MutableSonList set(int idx, boolean v) {
    set(idx, new MutableSonValue(SonType.BOOL, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, UTCMillisDate v) {
    set(idx, new MutableSonValue(SonType.DATE, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, UUID v) {
    set(idx, new MutableSonValue(SonType.DATE, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, char v) {
    set(idx, new MutableSonValue(SonType.CHAR, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, String v) {
    set(idx, new MutableSonValue(SonType.STRING, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, byte v) {
    set(idx, new MutableSonValue(SonType.BYTE, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, short v) {
    set(idx, new MutableSonValue(SonType.SHORT, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, int v) {
    set(idx, new MutableSonValue(SonType.INT, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, long v) {
    set(idx, new MutableSonValue(SonType.LONG, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, float v) {
    set(idx, new MutableSonValue(SonType.FLOAT, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, double v) {
    set(idx, new MutableSonValue(SonType.DOUBLE, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, SonBytes sb) {
    set(idx, new MutableSonValue(SonType.BYTES, sb));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, MutableSonList v) {
    set(idx, new MutableSonValue(SonType.LIST, v));
    return this;
  }

  @Override
  public synchronized MutableSonList set(int idx, MutableSonMap v) {
    set(idx, new MutableSonValue(SonType.MAP, v));
    return this;
  }

  @Override
  public synchronized MutableSonList setNull(int idx) {
    set(idx, MutableSonValue.NULL_VALUE);
    return this;
  }

  @Override
  public synchronized void sort(Comparator<? super SonValue> comparator) {
    entries.sort(comparator);
  }

  @Override
  public synchronized ByteBuffer toBuffer(NameSource ns) {
    SonStreamingListWriter<Void> w = Son.streamingListWriter(ns);
    appendTo(w);
    w.endList();
    ByteBuffer ret = w.buffer().getBuffer();
    ret.flip();
    return ret;
  }

  public synchronized void appendTo(SonStreamingListWriter<?> list) {
    for (MutableSonValue ent : this) {
      ent.getType().mutableListToBuffered(list, ent.getValue());
    }
  }

  @Override
  public synchronized void toBuffer(NameSource ns, ManagedBuffer dest) {
    SonStreamingListWriter<Void> w = Son.streamingListWriter(ns, dest);
    appendTo(w);
    w.endList();
  }

  @Override
  public String toString() {
    return Son.SONPrinters.SON.pretty().printList(this);
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    ByteBuffer dest = this.toBuffer();
    out.writeInt(dest.remaining());
    while (dest.hasRemaining()) {
      out.write(dest.get());
    }
  }
}
