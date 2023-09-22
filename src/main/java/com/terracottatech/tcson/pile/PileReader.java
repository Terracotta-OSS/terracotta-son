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
package com.terracottatech.tcson.pile;

import java.io.PrintWriter;
import java.nio.ByteBuffer;

/**
 * The Pile reader. Provides random access, by index, to a pile
 * stored as a ByteBuffer.
 */
public interface PileReader {

  /**
   * Read a boolean from the field index specified.
   *
   * @param idx the idx
   * @return the boolean
   */
  boolean bool(int idx);

  /**
   * Read a byte array value. This is a view ByteBuffer into the
   * underlying ByteBuffer.
   *
   * @param idx the idx
   * @return the byte buffer
   */
  ByteBuffer byteArray(int idx);

  /**
   * Pluck a particular byte from a byte array value.
   *
   * @param idx index of byte array field
   * @param offset the offset of the byte
   * @return the byte
   */
  byte byteArrayElement(int idx, int offset);

  /**
   * Return the length of the byte array for the specified field.
   *
   * @param idx the idx
   * @return the length
   */
  int byteArrayLength(int idx);

  /**
   * Return the user specified signifier for this byte array
   *
   * @param idx index
   * @return signifier for this index
   */
  byte byteArraySignifier(int idx);

  /**
   * Read a char from the specified field.
   *
   * @param idx the idx
   * @return the char
   */
  char chr(int idx);

  /**
   * Read a float (32 bit float) from the specified field.
   *
   * @param idx the idx
   * @return the float
   */
  float float32(int idx);

  /**
   * Read a double (64 bit float) from the specified field.
   *
   * @param idx the idx
   * @return the double
   */
  double float64(int idx);

  /**
   * Footprint in bytes.
   *
   * @return the int
   */
  int footprint();

  int getLimit();

  ByteBuffer getSourceBuffer();

  int getStartPosition();

  /**
   * Read a short (16 bit integer) from the specified field.
   *
   * @param idx the idx
   * @return the short
   */
  short int16(int idx);

  /**
   * Read an int (32 bit integer) from the specified field.
   *
   * @param idx the idx
   * @return the int
   */
  int int32(int idx);

  /**
   * Read a long (64 bit integer) from the specified field.
   *
   * @param idx the idx
   * @return the long
   */
  long int64(int idx);

  /**
   * Read a byte (8 bit integer) from the specified field.
   *
   * @param idx the idx
   * @return the byte
   */
  byte int8(int idx);

  /**
   * Is the index a null value.
   *
   * @param idx the idx
   * @return true if null.
   */
  boolean isNull(int idx);

  /**
   * Length (byte footprint) of a field index.
   *
   * @param idx the idx
   * @return the length
   */
  int lengthOf(int idx);

  /**
   * Open a new Pile 1 reader for a nested Pile.
   *
   * @param idx the index of the pile.
   * @return the new pile reader
   */
  PileReader pile(int idx);

  /**
   * Gets field count for this pile. Sub piles count as 1.
   *
   * @return the field count
   */
  int size();

  /**
   * Read a String from the specified field.
   *
   * @param idx the idx
   * @return the string
   */
  String str(int idx);

  void toString(String indent, PrintWriter pw);

  /**
   * Type of a field index value.
   *
   * @param idx the idx
   * @return the pile type
   */
  Pile.Type typeOf(int idx);

}
