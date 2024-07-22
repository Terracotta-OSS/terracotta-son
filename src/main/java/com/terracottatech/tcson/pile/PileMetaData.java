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
package com.terracottatech.tcson.pile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;

public class PileMetaData {
  private final int payloadSize;
  private final int start;
  private final int limit;
  private final ByteBuffer buffer;
  private final int[] typeAndPositionArray;
  private int lastSize;
  private int count;

  public PileMetaData(ByteBuffer go, int start, int limit) {
    this.buffer = go;
    int savePos = go.position();
    this.start = start;
    this.limit = limit;
    int op = go.get(limit - 1);
    int bytesForSize;
    int footprint;
    if ((op & 0b11) == 0b00) {
      bytesForSize = 1;
      footprint = (((op & 0xff) >>> 2)) & 0xffffff;
    } else if ((op & 0b11) == 0b01) {
      bytesForSize = 2;
      footprint = (go.getShort(limit - 2) >> 2) & 0xffffff;
    } else {
      bytesForSize = 4;
      footprint = (go.getInt(limit - 4) >> 2) & 0xffffff;
    }

    this.payloadSize = limit - start - bytesForSize - footprint;

    go.position(limit - bytesForSize - footprint);
    // turns out to be more efficient to pack everything in one array.
    // worst case is everything is in one byte
    int max = limit - go.position() - bytesForSize;
    this.typeAndPositionArray = new int[max];
    int pos = 0;
    int idx = 0;
    for (; go.position() < (limit - bytesForSize); ) {
      int p = go.get();
      int ord = p & Pile.Type.maxOrdinalValue();
      Pile.Type typ = Pile.Type.values()[ord];
      int thisSize;
      if (typ.isKnownSize()) {
        thisSize = typ.getKnownSize();
      } else if (p == ord) {
        thisSize = (int) VarInts.varDecode(go);
      } else {
        // -1 because zero length would be  prob
        thisSize = ((p >>> Pile.Type.bitWidth()) & Pile.Type.maxInlineValue()) - 1;
      }
      // keep track of the size of the last one.
      this.lastSize = thisSize;
      this.typeAndPositionArray[idx++] = pos | (typ.ordinal() << (32 - Pile.Type.bitWidth()));
      pos = pos + thisSize;
    }
    this.count = idx;
    go.position(savePos);
  }

  public ByteBuffer getBuffer() {
    return buffer;
  }

  public int getLength(int index) {
    if (index == count - 1) {
      return lastSize;
    }
    return relativePositionOf(index + 1) - relativePositionOf(index);
  }

  public int getPosition(int index) {
    return start + relativePositionOf(index);
  }

  public Pile.Type getType(int index) {
    return Pile.Type.values()[typeAndPositionArray[index] >>> (32 - Pile.Type.bitWidth())];
  }

  private int relativePositionOf(int index) {
    return typeAndPositionArray[index] & ((1 << (32 - Pile.Type.bitWidth())) - 1);
  }

  public int size() {
    return count;
  }

  private void toString(String indent, PrintWriter pw) {
    pw.println(indent + "Pile Meta: " + size() + "/" + (limit - start) + "/" + (limit - start - payloadSize));
    for (int i = 0; i < size(); i++) {
      pw.println(indent + getType(i) + " pos: " + getPosition(i) + " len: " + getLength(i));
      if (getType(i) == Pile.Type.PILE2 || getType(i) == Pile.Type.PILE1) {
        new PileReaderImpl(buffer, getPosition(i), getPosition(i) + getLength(i)).meta.toString(indent + "   ", pw);
      }
    }
  }

  @Override
  public String toString() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    toString("   ", pw);
    pw.flush();
    return sw.toString();
  }
}
