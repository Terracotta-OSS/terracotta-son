/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson;

import com.terracottatech.tcson.reading.ReadableSonValue;

import java.nio.ByteBuffer;

/**
 * A read only, buffer based SonMap. An implementation of this class
 * allows random access to values in the map.
 */
public interface ReadableSonMap extends SonMap<ReadableSonValue.MapValue> {

  @Override
  ReadableSonValue asSonValue();

  /**
   * Byte footprint.
   *
   * @return the int
   */
  int footprint();

  NameSource getNameSource();

  /**
   * Reset this to preside over a new buffer.
   *
   * @param buf the new buffer
   * @return the son map
   */
  default ReadableSonMap reset(ByteBuffer buf) {
    return reset(buf, buf.position(), buf.limit());
  }

  /**
   * Reset over a new buffer with specific guards...
   *
   * @param buf the buffer
   * @param start the start position (absolute)
   * @param limit the limit (absolute)
   * @return the son map
   */
  ReadableSonMap reset(ByteBuffer buf, int start, int limit);

  ByteBuffer toBuffer();

  void toBuffer(ByteBuffer dest);
}
