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
package com.terracottatech.tcson.writing;

import com.terracottatech.tcson.NameSource;
import com.terracottatech.tcson.UTCMillisDate;
import com.terracottatech.tcson.pile.ManagedBuffer;
import com.terracottatech.tcson.pile.Pile;
import com.terracottatech.tcson.pile.PileWriter;
import com.terracottatech.tcson.pile.PileWriterImpl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class SonStreamingMapWriter<E> {

  static class IndexDescr implements Comparable<IndexDescr> {
    private final int id;
    private final int index;

    IndexDescr(int id, int index) {
      this.id = id;
      this.index = index;
    }

    @Override
    public int compareTo(IndexDescr o) {
      return Integer.compare(id, o.id);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      IndexDescr descr = (IndexDescr) o;
      return id == descr.id;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }

    @Override
    public String toString() {
      return "IndexDescr{" + "id=" + id + ", index=" + index + '}';
    }
  }
  private final GlobalNameMapWriter globalNameMap;
  private final HashSet<String> localNames = new HashSet<>();
  private final ArrayList<IndexDescr> localIds = new ArrayList<>();
  private final E parent;
  private final NameSource nameSource;
  private final PileWriter writer;
  private byte[] tmpArray = new byte[32];
  private ByteBuffer tmpBuffer = ByteBuffer.wrap(tmpArray);

  public SonStreamingMapWriter(NameSource nameSource) {
    this(nameSource, new ManagedBuffer(1024));
  }

  public SonStreamingMapWriter(NameSource nameSource, ManagedBuffer b) {
    this(null, nameSource, new PileWriterImpl(Pile.Type.PILE1, b), new GlobalNameMapWriter());
  }

  SonStreamingMapWriter(E parent, NameSource nameSource, PileWriter pw, GlobalNameMapWriter globalMap) {
    this.parent = parent;
    this.nameSource = nameSource;
    this.writer = pw;
    this.globalNameMap = globalMap;
  }

  public SonStreamingMapWriter() {
    this(null, new ManagedBuffer(1024));
  }

  public SonStreamingMapWriter<E> append(String name, boolean v) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.bool(v);
    localIds.add(descr);
    return this;
  }

  private int idOf(String name) {
    if (!localNames.add(name)) {
      throw new IllegalArgumentException();
    }
    return globalNameMap.allocateId(name);
  }

  public SonStreamingMapWriter<E> append(String name, short v) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.int16(v);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String name, byte v) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.int8(v);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String name, int v) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.zigzag32(v);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String name, long v) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.zigzag64(v);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String name, float v) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.float32(v);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String name, double v) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.float64(v);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String name, String v) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.str(v);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String name, char v) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.chr(v);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String name, byte signifier, ByteBuffer buf) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.byteArray(signifier, buf);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String name, UTCMillisDate sd) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    tmpBuffer.clear();
    tmpBuffer.putLong(sd.utcMillis());
    tmpBuffer.flip();
    writer.byteArray(SonWriter.DATE_SIGNIFIER, tmpBuffer);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String name, UUID uuid) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    tmpBuffer.clear();
    tmpBuffer.putLong(uuid.getMostSignificantBits());
    tmpBuffer.putLong(uuid.getLeastSignificantBits());
    tmpBuffer.flip();
    writer.byteArray(SonWriter.UUID_SIGNIFIER, tmpBuffer);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> append(String key, byte signifier, byte[] value) {
    return append(key, signifier, value, 0, value.length);
  }

  public SonStreamingMapWriter<E> append(String name, byte signifier, byte[] arr, int off, int len) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.byteArray(signifier, arr, off, len);
    localIds.add(descr);
    return this;
  }

  public SonStreamingMapWriter<E> appendNull(String name) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    writer.nullValue();
    localIds.add(descr);
    return this;
  }

  public ManagedBuffer buffer() {
    return writer.managedBuffer();
  }

  public E endMap() {
    // next write out the ids in order, pos 0 holds id of value 0
    PileWriter w = writer.pile(Pile.Type.PILE1);
    for (IndexDescr p : localIds) {
      w.zigzag32(p.id);
    }
    w.endPile();

    if (parent == null) {
      writeGlobalMap(nameSource, globalNameMap, writer);
    }

    // end the writer.
    writer.endPile();

    // return the parent. sigh.
    return parent;
  }

  static void writeGlobalMap(NameSource nameSource, GlobalNameMapWriter globalNameMap, PileWriter writer) {
    PileWriter w = writer.pile(Pile.Type.PILE1);
    for (String str : globalNameMap.getNamesInOrder()) {
      if (nameSource != null) {
        Long p = nameSource.idOf(str);
        if (p == null) {
          w.str(str);
        } else {
          w.zigzag64(p);
        }
      } else {
        w.str(str);
      }
    }
    w.endPile();
  }

  public E getParent() {
    return parent;
  }

  public SonStreamingListWriter<SonStreamingMapWriter<E>> list(String name) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    PileWriter ret = writer.pile(Pile.Type.PILE2);
    localIds.add(descr);
    return new SonStreamingListWriter<>(this, nameSource, ret, globalNameMap);
  }

  public SonStreamingMapWriter<SonStreamingMapWriter<E>> map(String name) {
    // record name::id for global table
    int id = idOf(name);
    IndexDescr descr = new IndexDescr(id, writer.size());
    PileWriter ret = writer.pile(Pile.Type.PILE1);
    localIds.add(descr);
    return new SonStreamingMapWriter<>(this, nameSource, ret, globalNameMap);
  }

  public void reset() {
    globalNameMap.clear();
    localIds.clear();
    localNames.clear();
    writer.reset();
  }

}
