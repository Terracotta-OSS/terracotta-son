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
package com.terracottatech.tcson.reading;

import com.terracottatech.tcson.MutableSonList;
import com.terracottatech.tcson.MutableSonMap;
import com.terracottatech.tcson.NameSource;
import com.terracottatech.tcson.ReadableSonList;
import com.terracottatech.tcson.ReadableSonMap;
import com.terracottatech.tcson.Son;
import com.terracottatech.tcson.SonBytes;
import com.terracottatech.tcson.SonMap;
import com.terracottatech.tcson.SonMapValue;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.mutable.MutableSonMapImpl;
import com.terracottatech.tcson.pile.Pile;
import com.terracottatech.tcson.pile.PileReader;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class ReadableSonMapImpl implements ReadableSonMap {
  private GlobalNameMapReader globalNameMap;
  private PileReader root;
  private PileReader keysPile;
  private int count;
  private IntIntMap idToIndex = null;
  private HashMap<String, Integer> nameToIndexCache = new HashMap<>();

  public ReadableSonMapImpl(ByteBuffer buf) {
    this(null, buf);
  }

  public ReadableSonMapImpl(NameSource nameSource, ByteBuffer buf) {
    this(nameSource, buf, buf.position(), buf.limit());
  }

  public ReadableSonMapImpl(NameSource nameSource, ByteBuffer buf, int start, int limit) {
    init(nameSource, buf, start, limit);
  }

  private void init(NameSource nameSource, ByteBuffer buf, int start, int limit) {
    this.root = Pile.reader(buf, start, limit);
    // ok, this is split in 3 parts
    // values in order
    // for each value, the name id for that one
    // finally, dictionary of <name>, <id> pairs
    // last entry is count of dictionary pairs
    int cnt = root.size();
    PileReader globalNamePile = root.pile(cnt - 1);
    this.globalNameMap = new GlobalNameMapReader(nameSource, globalNamePile);
    this.keysPile = root.pile(cnt - 2);
    this.count = keysPile.size();
  }

  public ReadableSonMapImpl(GlobalNameMapReader nameMap, PileReader upd) {
    this.root = upd;
    this.globalNameMap = nameMap;
    this.keysPile = upd.pile(upd.size() - 1);
    this.count = keysPile.size();
  }

  @Override
  public MutableSonMap asMutable() {
    MutableSonMap m = new MutableSonMapImpl();
    for (SonMapValue ent : this) {
      switch (ent.getType()) {
        case LIST:
          ReadableSonList l = (ReadableSonList) ent.getValue();
          MutableSonList ml = l.asMutable();
          m.put(ent.getKey(), ent.getType(), ml);
          break;
        case MAP:
          ReadableSonMap rm = (ReadableSonMap) ent.getValue();
          MutableSonMap mm = rm.asMutable();
          m.put(ent.getKey(), ent.getType(), mm);
          break;
        case BYTES:
          m.put(ent.getKey(), ent.getType(), ((SonBytes) ent.getValue()).dup());
          break;
        default:
          m.put(ent.getKey(), ent.getType(), ent.getValue());
          break;
      }
    }
    return m;
  }

  @Override
  public ReadableSonValue asSonValue() {
    return new ReadableSonValue(SonType.MAP, this);
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
  public int footprint() {
    return root.footprint();
  }

  @Override
  public ReadableSonValue.MapValue get(String name) {
    int idx = indexForName(name);
    if (idx >= 0) {
      return indexedGetAt(name, globalNameMap, root, idx);
    }
    return null;
  }

  static ReadableSonValue.MapValue indexedGetAt(String name, GlobalNameMapReader nameMap, PileReader root, int idx) {
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
      case NULL:
        typ = SonType.NULL;
        val = null;
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
      default:
        throw new IllegalStateException();
    }
    return new ReadableSonValue.MapValue(name, typ, val);
  }

  private int indexForName(String name) {
    Integer probe = nameToIndexCache.get(name);
    if (probe == null) {
      int id = globalNameMap.lookupId(name);
      if (id >= 0) {
        populateIdToIndexMap();
        id = idToIndex.get(id);
        if (id >= 0) {
          nameToIndexCache.put(name, id);
        }
      }
      return id;
    }
    return probe;
  }

  private void populateIdToIndexMap() {
    if (idToIndex == null) {
      int kcnt = keysPile.size();
      IntIntMap m2 = new IntIntMap(-1, kcnt + 10);
      for (int i = 0; i < kcnt; i++) {
        m2.put(keysPile.int32(i), i);
      }
      idToIndex = m2;
    }
  }

  @Override
  public NameSource getNameSource() {
    return globalNameMap.getNameSource();
  }

  @Override
  public int hashCode() {
    return deepHashCode();
  }

  @Override
  public Iterator<ReadableSonValue.MapValue> iterator() {
    final HashMap<Integer, String> hm = new HashMap<>();
    for (Map.Entry<String, Integer> ent : globalNameMap.getNamesToId().entrySet()) {
      hm.put(ent.getValue(), ent.getKey());
    }
    return new Iterator<ReadableSonValue.MapValue>() {

      private int current = 0;

      @Override
      public ReadableSonValue.MapValue next() {
        if (hasNext()) {
          int idx = current;
          int id = keysPile.int32(current++);
          String nm = hm.get(id);
          ReadableSonValue.MapValue ret = indexedGetAt(nm, globalNameMap, root, idx);
          return ret;
        }
        throw new NoSuchElementException();
      }

      @Override
      public boolean hasNext() {
        return current < keysPile.size();
      }
    };
  }

  public ReadableSonMapImpl reset(NameSource nameSource, ByteBuffer buf, int start, int limit) {
    init(nameSource, buf, start, limit);
    return this;
  }

  public ReadableSonMapImpl reset(ByteBuffer buf, int start, int limit) {
    init(globalNameMap.getNameSource(), buf, start, limit);
    return this;
  }

  @Override
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
    return Son.SONPrinters.SON.pretty().printMap(this);
  }
}
