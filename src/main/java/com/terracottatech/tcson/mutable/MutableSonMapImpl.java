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
package com.terracottatech.tcson.mutable;

import com.terracottatech.tcson.MutableSonList;
import com.terracottatech.tcson.MutableSonMap;
import com.terracottatech.tcson.NameSource;
import com.terracottatech.tcson.Son;
import com.terracottatech.tcson.SonBytes;
import com.terracottatech.tcson.SonMap;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.UTCMillisDate;
import com.terracottatech.tcson.pile.ManagedBuffer;
import com.terracottatech.tcson.writing.SonStreamingMapWriter;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;

public class MutableSonMapImpl implements MutableSonMap {

  private static final long serialVersionUID = -2312184814185347482L;

  private LinkedHashMap<String, MutableSonValue.MapValue> map = new LinkedHashMap<>();

  @Override
  public MutableSonValue asSonValue() {
    return new MutableSonValue(SonType.MAP, this);
  }

  @Override
  public synchronized MutableSonMap clear() {
    map.clear();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof SonMap) {
      return deepEquals((SonMap<?>) o);
    }
    return false;
  }

  @Override
  public synchronized MutableSonValue.MapValue get(String name) {
    return map.get(name);
  }

  public int hashCode() {
    return deepHashCode();
  }

  @Override
  public synchronized Iterator<MutableSonValue.MapValue> iterator() {
    Iterator<MutableSonValue.MapValue> ret = new ArrayList<>(map.values()).iterator();
    return ret;
  }

  @Override
  public synchronized MutableSonMap put(String name, boolean v) {
    map.put(name, new MutableSonValue.MapValue(name, SonType.BOOL, v));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, byte v) {
    map.put(name, new MutableSonValue.MapValue(name, SonType.BYTE, v));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, UTCMillisDate v) {
    map.put(name, new MutableSonValue.MapValue(name, SonType.DATE, v));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, UUID v) {
    map.put(name, new MutableSonValue.MapValue(name, SonType.UUID, v));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, short v) {
    map.put(name, new MutableSonValue.MapValue(name, SonType.SHORT, v));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, int v) {
    map.put(name, new MutableSonValue.MapValue(name, SonType.INT, v));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, char v) {
    map.put(name, new MutableSonValue.MapValue(name, SonType.CHAR, v));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, long v) {
    map.put(name, new MutableSonValue.MapValue(name, SonType.LONG, v));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, float v) {
    map.put(name, new MutableSonValue.MapValue(name, SonType.FLOAT, v));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, double v) {
    map.put(name, new MutableSonValue.MapValue(name, SonType.DOUBLE, v));
    return this;
  }

  @Override
  public MutableSonMap put(String name, byte signifier, ByteBuffer buf) {
    return put(name, new SonBytes(signifier, buf));
  }

  @Override
  public MutableSonMap put(String name, SonBytes sb) {
    Objects.requireNonNull(sb);
    this.map.put(name, new MutableSonValue.MapValue(name, SonType.BYTES, sb));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, String value) {
    Objects.requireNonNull(value);
    this.map.put(name, new MutableSonValue.MapValue(name, SonType.STRING, value));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, MutableSonMap map) {
    Objects.requireNonNull(map);
    this.map.put(name, new MutableSonValue.MapValue(name, SonType.MAP, map));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(String name, MutableSonList ml) {
    Objects.requireNonNull(map);
    this.map.put(name, new MutableSonValue.MapValue(name, SonType.LIST, ml));
    return this;
  }

  @Override
  public synchronized MutableSonMap put(MutableSonValue.MapValue m) {
    map.put(m.getKey(), m);
    return this;
  }

  @Override
  public synchronized MutableSonMap putNull(String name) {
    Objects.requireNonNull(map);
    this.map.put(name, new MutableSonValue.MapValue(name, SonType.NULL, null));
    return this;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException {
    int len = in.readInt();
    byte[] dest = new byte[len];
    in.read(dest);
    MutableSonMapImpl newMap = (MutableSonMapImpl) Son.readableMap(ByteBuffer.wrap(dest)).asMutable();
    this.map = newMap.map;
  }

  @Override
  public synchronized MutableSonMap remove(String name) {
    map.remove(name);
    return this;
  }

  public synchronized int size() {
    return map.size();
  }

  @Override
  public synchronized ByteBuffer toBuffer(NameSource ns) {
    SonStreamingMapWriter<Void> w = Son.streamingMapWriter(ns);
    appendTo(w);
    w.endMap();
    ByteBuffer ret = w.buffer().getBuffer();
    ret.flip();
    return ret;
  }

  public synchronized void appendTo(SonStreamingMapWriter<?> mw) {
    for (MutableSonValue.MapValue ent : this) {
      ent.getType().mutableMapToBuffered(mw, ent.getKey(), ent.getValue());
    }
  }

  @Override
  public synchronized void toBuffer(NameSource ns, ManagedBuffer dest) {
    SonStreamingMapWriter<Void> w = Son.streamingMapWriter(ns, dest);
    appendTo(w);
    w.endMap();
  }

  @Override
  public synchronized String toString() {
    return Son.SONPrinters.SON.pretty().printMap(this);
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
