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
