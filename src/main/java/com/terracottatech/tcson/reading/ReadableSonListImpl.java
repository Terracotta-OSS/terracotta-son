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
package com.terracottatech.tcson.reading;

import com.terracottatech.tcson.MutableSonList;
import com.terracottatech.tcson.NameSource;
import com.terracottatech.tcson.ReadableSonList;
import com.terracottatech.tcson.Son;
import com.terracottatech.tcson.SonList;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.mutable.MutableSonListImpl;
import com.terracottatech.tcson.pile.Pile;
import com.terracottatech.tcson.pile.PileReader;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ReadableSonListImpl implements ReadableSonList {
  private final PileReader root;
  private final int count;
  private final GlobalNameMapReader nameMap;

  public ReadableSonListImpl(NameSource nameSource, ByteBuffer buf) {
    this(nameSource, buf, buf.position(), buf.limit());
  }

  public ReadableSonListImpl(NameSource nameSource, ByteBuffer buf, int start, int limit) {
    this.root = Pile.reader(buf, start, limit);
    int cnt = root.size();
    this.count = cnt - 1;
    this.nameMap = new GlobalNameMapReader(nameSource, root.pile(cnt - 1));
  }

  public ReadableSonListImpl(GlobalNameMapReader nameMap, ByteBuffer buf, int start, int limit) {
    this(nameMap, Pile.reader(buf, start, limit));
  }

  public ReadableSonListImpl(GlobalNameMapReader nameMap, PileReader upd) {
    this.nameMap = nameMap;
    this.root = upd;
    this.count = root.size();
  }

  @Override
  public MutableSonList asMutable() {
    MutableSonList ml = new MutableSonListImpl();
    for (ReadableSonValue ent : this) {
      switch (ent.getType()) {
        case LIST:
          ml.add(ent.getType(), ent.listValue().asMutable());
          break;
        case MAP:
          ml.add(ent.getType(), ent.mapValue().asMutable());
          break;
        default:
          ml.add(ent.getType(), ent.getValue());
          break;
      }
    }
    return ml;
  }

  @Override
  public ReadableSonValue asSonValue() {
    return new ReadableSonValue(SonType.LIST, this);
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

  @Override
  public ReadableSonValue get(int idx) {
    return indexedGetAt(idx);
  }

  @Override
  public NameSource getNameSource() {
    return nameMap.getNameSource();
  }

  @Override
  public int hashCode() {
    return deepHashCode();
  }

  @Override
  public Iterator<ReadableSonValue> iterator() {
    return new Iterator<ReadableSonValue>() {
      private int current = 0;

      @Override
      public ReadableSonValue next() {
        if (hasNext()) {
          return indexedGetAt(current++);
        }
        throw new NoSuchElementException();
      }

      @Override
      public boolean hasNext() {
        return current < size();
      }
    };
  }

  private ReadableSonValue indexedGetAt(int idx) {
    if (idx >= count) {
      throw new ArrayIndexOutOfBoundsException(idx + " vs " + count);
    }
    Object val;
    SonType typ;
    switch (root.typeOf(idx)) {
      case PILE1:
        PileReader rr = root.pile(idx);
        val = new ReadableSonMapImpl(nameMap, rr);
        typ = SonType.MAP;
        break;
      case PILE2:
        rr = root.pile(idx);
        val = new ReadableSonListImpl(nameMap, rr);
        typ = SonType.LIST;
        break;
      case STRING:
        typ = SonType.STRING;
        val = root.str(idx);
        break;
      case CHAR:
        val = root.chr(idx);
        typ = SonType.CHAR;
        break;
      case INT8:
        typ = SonType.BYTE;
        val = (byte) root.int32(idx);
        break;
      case INT16:
        typ = SonType.SHORT;
        val = (short) root.int32(idx);
        break;
      case INT32:
      case ZIGZAG32:
        typ = SonType.INT;
        val = root.int32(idx);
        break;
      case INT64:
      case ZIGZAG64:
        typ = SonType.LONG;
        val = root.int64(idx);
        break;
      case FLOAT32:
        typ = SonType.FLOAT;
        val = root.float32(idx);
        break;
      case FLOAT64:
        typ = SonType.DOUBLE;
        val = root.float64(idx);
        break;
      case BOOLEAN:
        typ = SonType.BOOL;
        val = root.bool(idx);
        break;
      case BYTE_ARRAY:
        ByteBuffer bb = root.byteArray(idx);
        byte sig = root.byteArraySignifier(idx);
        typ = ReadableSonValue.typeFromSignifier(sig);
        val = ReadableSonValue.formFromBytes(sig, bb);
        break;
      case NULL:
        typ = SonType.NULL;
        val = null;
        break;
      default:
        throw new IllegalStateException();
    }
    return new ReadableSonValue(typ, val);
  }

  public int size() {
    return count;
  }

  @Override
  public void toBuffer(ByteBuffer dest) {
    dest.put(toBuffer());
  }

  @Override
  public ByteBuffer toBuffer() {
    ByteBuffer ret = root.getSourceBuffer().slice();
    ret.position(root.getStartPosition()).limit(root.getLimit());
    return ret;
  }

  public String toString() {
    return Son.SONPrinters.SON.pretty().printList(this);
  }

}
