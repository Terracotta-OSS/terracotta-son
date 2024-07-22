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
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

public class PileReaderImpl implements PileReader {
  protected final ByteBuffer src;
  protected final PileMetaData meta;
  private final int start;
  private final int limit;

  public PileReaderImpl(ByteBuffer src) {
    this(src, src.position(), src.limit());
  }

  public PileReaderImpl(ByteBuffer src, int start, int limit) {
    if (src.order() != ByteOrder.BIG_ENDIAN) {
      throw new IllegalArgumentException();
    }
    this.src = src;
    this.start = start;
    this.limit = limit;
    this.meta = new PileMetaData(src, start, limit);
  }

  @Override
  public boolean bool(int idx) {
    checkType(idx, Pile.Type.BOOLEAN);
    return src.get(positionOf(idx)) != 0;
  }

  @Override
  public ByteBuffer byteArray(int idx) {
    checkType(idx, Pile.Type.BYTE_ARRAY);
    ByteBuffer b = src.duplicate();
    b.position(positionOf(idx) + 1).limit(b.position() + lengthOf(idx) - 1);
    return b.asReadOnlyBuffer();
  }

  @Override
  public byte byteArrayElement(int idx, int offset) {
    checkType(idx, Pile.Type.BYTE_ARRAY);
    return src.get(positionOf(idx) + offset + 1);
  }

  @Override
  public int byteArrayLength(int idx) {
    checkType(idx, Pile.Type.BYTE_ARRAY);
    return lengthOf(idx) - 1;
  }

  @Override
  public int lengthOf(int idx) {
    return meta.getLength(idx);
  }

  @Override
  public byte byteArraySignifier(int idx) {
    checkType(idx, Pile.Type.BYTE_ARRAY);
    return src.get(positionOf(idx));
  }

  @Override
  public char chr(int idx) {
    checkType(idx, Pile.Type.CHAR);
    int p = positionOf(idx);
    return src.getChar(p);
  }

  @Override
  public float float32(int idx) {
    checkType(idx, Pile.Type.FLOAT32);
    int asInt = src.getInt(positionOf(idx));
    return Float.intBitsToFloat(asInt);
  }

  @Override
  public double float64(int idx) {
    checkType(idx, Pile.Type.FLOAT64);
    long asLong = src.getLong(positionOf(idx));
    return Double.longBitsToDouble(asLong);
  }

  @Override
  public int footprint() {
    return limit - start;
  }

  @Override
  public int getLimit() {
    return limit;
  }

  @Override
  public ByteBuffer getSourceBuffer() {
    return src;
  }

  @Override
  public int getStartPosition() {
    return start;
  }

  @Override
  public short int16(int idx) {
    checkType(idx, Pile.Type.INT16);
    return src.getShort(positionOf(idx));
  }

  @Override
  public int int32(int idx) {
    int p = positionOf(idx);
    switch (meta.getType(idx)) {
      case INT8:
        return src.get(p);
      case INT16:
        return src.getShort(p);
      case INT32:
        return src.getInt(p);
      case ZIGZAG32:
        return (int) VarInts.zigzagDecode(src, p);
      default:
        throw new IllegalArgumentException("" + meta.getType(idx));
    }
  }

  @Override
  public long int64(int idx) {
    int p = positionOf(idx);
    switch (meta.getType(idx)) {
      case INT8:
        return src.get(p);
      case INT16:
        return src.getShort(p);
      case INT32:
        return src.getInt(p);
      case INT64:
        return src.getLong(p);
      case ZIGZAG32:
      case ZIGZAG64:
        return VarInts.zigzagDecode(src, p);
      default:
        throw new IllegalArgumentException(meta.getType(idx).name());
    }
  }

  @Override
  public byte int8(int idx) {
    checkType(idx, Pile.Type.INT8);
    return src.get(positionOf(idx));
  }

  public int positionOf(int idx) {
    return meta.getPosition(idx);
  }

  protected void checkType(int idx, Pile.Type type) {
    if (meta.getType(idx) != type) {
      throw new ClassCastException();
    }
  }

  @Override
  public boolean isNull(int idx) {
    return (meta.getType(idx).equals(Pile.Type.NULL));
  }

  @Override
  public PileReaderImpl pile(int idx) {
    Pile.Type mt = meta.getType(idx);
    if (mt != Pile.Type.PILE1 && mt != Pile.Type.PILE2) {
      throw new ClassCastException();
    }
    int p = positionOf(idx);
    int l = lengthOf(idx);
    return new PileReaderImpl(src, p, p + l);
  }

  @Override
  public int size() {
    return meta.size();
  }

  @Override
  public String str(int idx) {
    checkType(idx, Pile.Type.STRING);
    ByteBuffer b = src.slice();
    b.position(positionOf(idx));
    try {
      return StringTool.decodeString(b, lengthOf(idx));
    } catch (UTFDataFormatException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    toString("", pw);
    pw.flush();
    return sw.toString();
  }

  @Override
  public void toString(String indent, PrintWriter pw) {
    pw.println(indent + "Pile: Meta: " + meta.size() + "/" + (limit - start));
    for (int i = 0; i < meta.size(); i++) {
      pw.print(indent + meta.getType(i) + " pos: " + meta.getPosition(i) + " len: " + meta.getLength(i));
      switch (meta.getType(i)) {
        case PILE1:
        case PILE2:
          pw.println();
          new PileReaderImpl(meta.getBuffer(), meta.getPosition(i), meta.getPosition(i) + meta.getLength(i)).toString(
            indent +
            "   ", pw);
          break;
        case STRING:
          pw.println(" = " + str(i));
          break;
        case NULL:
          pw.println(" = null");
          break;
        case CHAR:
          pw.println(" = " + chr(i));
          break;
        case BOOLEAN:
          pw.println(" = " + bool(i));
          break;
        case FLOAT64:
          pw.println(" = " + float64(i));
          break;
        case BYTE_ARRAY:
          byte[] b = new byte[meta.getLength(i)];
          byteArray(i).get(b);
          pw.println(" = " + byteArraySignifier(i) + "#" + Base64.getEncoder().encodeToString(b));
          break;
        case FLOAT32:
          pw.println(" = " + float32(i));
          break;
        case INT32:
        case INT16:
        case INT8:
        case ZIGZAG32:
          pw.println(" = " + int32(i));
          break;
        case INT64:
        case ZIGZAG64:
          pw.println(" = " + int64(i));
          break;
        default:
          throw new IllegalStateException();

      }
    }
  }

  @Override
  public Pile.Type typeOf(int idx) {
    return meta.getType(idx);
  }
}
