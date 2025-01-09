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

public class Pile {
  /*
   * The theory here is that we want a fairly comprehensive list
   * (all the primitives for sure), then some variable length ones.
   * VarInts were easy to pick. signified byte arrays were also easy,
   * as most byte arrays (we assume) will be longer than a few bytes, so
   * the extra byte is not big deal. String could have been done as
   * byte arrays, but we use them so much, it is worth being a first
   * class type. The extra oddball is Nulls. Given the logic of
   * embedded the size in the same byte as the type if possible, fewer
   * types aren't helpful unless we could get to 8, which is not really
   * doable. But we are comfortably under 16 here, allowing for 4 byte
   * embedded sizes as well, which means even worst case var ints need no
   * extra size byte. Note that for the embedded size, we need to add +1 to it,
   * as a size of 0 would be the same as no embedded size. Gnarly bug.
   *
   */
  public enum Type {
    INT8(1),
    INT16(2),
    INT32(4),
    INT64(8),
    FLOAT64(8),
    FLOAT32(4),
    BOOLEAN(1),
    CHAR(2),
    NULL(1),
    ZIGZAG32,
    ZIGZAG64,
    STRING,
    BYTE_ARRAY,
    PILE1,
    PILE2;

    static {
      // if we add too many, things will die.
      if (Type.values().length > maxOrdinalValue()) {
        // this is sort of catastrophic. obvs.
        System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        new IllegalStateException("Pile.Type sizing error! " +
                                  Type.values().length +
                                  " > " +
                                  maxOrdinalValue()).printStackTrace(System.err);
        System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
      }
    }

    private int knownSize;

    Type() {
      this(0);
    }

    Type(int knownSize) {
      this.knownSize = knownSize;
    }

    public static int maxInlineValue() {
      return (1 << 8 - Pile.Type.bitWidth()) - 1;
    }

    public static int bitWidth() {
      return 4;
    }

    public static int maxOrdinalValue() {
      return (1 << Pile.Type.bitWidth()) - 1;
    }

    public int getKnownSize() {
      return knownSize;
    }

    public boolean isKnownSize() {
      return knownSize > 0;
    }

  }

  private Pile() {
  }

  public static PileWriter writer(Pile.Type pileType) {
    return new PileWriterImpl(pileType);
  }

  public static PileWriter writer(Pile.Type pileType, int initial) {
    return new PileWriterImpl(pileType, initial);
  }

  public static PileReader reader(ByteBuffer b) {
    return new PileReaderImpl(b);
  }

  public static PileReader reader(ByteBuffer b, int start, int limit) {
    return new PileReaderImpl(b, start, limit);
  }

}
