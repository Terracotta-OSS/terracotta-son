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

import java.nio.ByteBuffer;

/**
 * The Pile writer interface. Used to write a pile, in order, to a managed buffer.
 *
 * @author cschanck
 */
public interface PileWriter {

  /**
   * Write a boolean.
   *
   * @param v the v
   * @return the pile writer
   */
  PileWriter bool(boolean v);

  /**
   * Write a portion of a byte array.
   *
   * @param b the b
   * @param off the off
   * @param len the len
   * @return the pile writer
   */
  PileWriter byteArray(byte signifier, byte[] b, int off, int len);

  /**
   * Write a byte array.
   *
   * @param buf the buf
   * @return the pile writer
   */
  PileWriter byteArray(byte signifier, ByteBuffer buf);

  /**
   * Write a character
   *
   * @param c the c
   * @return the pile writer
   */
  PileWriter chr(char c);

  /**
   * End the current pile writer.
   *
   * @return the parent pile writer
   */
  PileWriter endPile();

  /**
   * Write a 32 bit float (Float).
   *
   * @param v the v
   * @return the pile writer
   */
  PileWriter float32(float v);

  /**
   * Write a 64 bit float (Double).
   *
   * @param v the v
   * @return the pile writer
   */
  PileWriter float64(double v);

  /**
   * Write a 16 bit signed integer (Short).
   *
   * @param v the v
   * @return the pile writer
   */
  PileWriter int16(short v);

  /**
   * Write an 32 bit signed integer (Integer).
   *
   * @param v the v
   * @return the pile writer
   */
  PileWriter int32(int v);

  /**
   * Write a 64 bit signed integer, in encoded fashion.
   *
   * @param v the v
   * @return the pile writer
   */
  PileWriter int64(long v);

  /**
   * Write an 8 bit signed integer (Byte).
   *
   * @param v the v
   * @return the pile writer
   */
  PileWriter int8(byte v);

  ManagedBuffer managedBuffer();

  /**
   * Write a null signifier.
   *
   * @return the pile writer
   */
  PileWriter nullValue();

  /**
   * Open a new Pile object.
   *
   * @param pileType type
   * @return the new pile writer
   */
  PileWriter pile(Pile.Type pileType);

  /**
   * Reset the underlying byte buffer, prepare for writes.
   */
  void reset();

  void reset(ByteBuffer b);

  /**
   * Gets field count in this pile (sub piles count as 1 field).
   *
   * @return the field count
   */
  int size();

  /**
   * Write String value.
   *
   * @param s the s
   * @return the pile writer
   */
  PileWriter str(String s);

  /**
   * Zigzag 32 pile writer.
   *
   * @param v the v
   * @return the pile writer
   */
  PileWriter zigzag32(int v);

  /**
   * Write a 64 bit signed integer, in encoded fashion.
   *
   * @param v the v
   * @return the pile writer
   */
  PileWriter zigzag64(long v);
}
