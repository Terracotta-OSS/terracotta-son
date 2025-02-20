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
import java.util.Arrays;

/**
 * Metadata for Piles while being written. Tracks type/size as they
 * are added. Writes highly dense metadata stanza as needed.
 */
public class PileWriterMetaData {
  private int startPos;
  private Pile.Type[] types;
  private int[] sizes;
  private int count = 0;

  public PileWriterMetaData(int initial) {
    this.types = new Pile.Type[initial];
    this.sizes = new int[initial];
  }

  public void add(Pile.Type t, int fieldByteSize) {
    ensureRoom();
    types[count] = t;
    sizes[count++] = fieldByteSize;
  }

  private void ensureRoom() {
    if (count == sizes.length) {
      int newSize = types.length + 64;
      this.types = Arrays.copyOf(types, newSize);
      this.sizes = Arrays.copyOf(sizes, newSize);
    }
  }

  public void clear() {
    count = 0;
  }

  public int getStartPos() {
    return startPos;
  }

  public int maxFootprint() {
    return size() * 10 + 5;
  }

  public void setStartPos(int startPos) {
    this.startPos = startPos;
  }

  /**
   * Writes out meta data stanza.
   * Compressed type and size for each value, plus 1,2, or 4 byte footprint.
   */
  public void writeDirectory(ByteBuffer dest) {
    // write out end
    int pos = dest.position();
    for (int i = 0; i < size(); i++) {
      Pile.Type typ = getType(i);
      int sz = getSize(i);
      if (!typ.isKnownSize()) {
        // bit of cleverness. if small enough, stuff it in the upper 3 bits
        // of the ordinal byte.
        if ((sz + 1) <= Pile.Type.maxInlineValue()) {
          byte tmp = (byte) ((sz + 1) << Pile.Type.bitWidth() | typ.ordinal());
          dest.put(tmp);
        } else {
          dest.put((byte) typ.ordinal());
          VarInts.varEncode(dest, sz);
        }
      } else {
        dest.put((byte) typ.ordinal());
      }
    }
    int sz = dest.position() - pos;
    if (sz < (1 << 6)) {
      dest.put((byte) (sz << 2));
    } else if (sz < (1 << 14)) {
      dest.putShort((short) ((sz << 2) | 0b01));
    } else {
      dest.putInt((sz << 2) | 0b10);
    }
  }

  public Pile.Type getType(int index) {
    return types[index];
  }

  public int getSize(int index) {
    return sizes[index];
  }

  public int size() {
    return count;
  }
}
