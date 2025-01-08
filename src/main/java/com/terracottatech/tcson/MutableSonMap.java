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
package com.terracottatech.tcson;

import com.terracottatech.tcson.mutable.MutableSonMapBuilder;
import com.terracottatech.tcson.mutable.MutableSonValue;
import com.terracottatech.tcson.pile.ManagedBuffer;

import java.io.Externalizable;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.UUID;

/**
 * Mutable SonMap, allows for mutation of the map in a
 * strongly typed way. Can be then turned into a snapshot
 * buffer on demand..
 */
public interface MutableSonMap extends SonMap<MutableSonValue.MapValue>, Externalizable {
  long serialVersionUID = 420L;

  @Override
  default MutableSonMap asMutable() {
    return this;
  }

  @Override
  MutableSonValue asSonValue();

  default MutableSonMapBuilder builder() {
    return new MutableSonMapBuilder(this);
  }

  MutableSonMap clear();

  MutableSonMap put(String name, boolean v);

  MutableSonMap put(String name, byte v);

  MutableSonMap put(String name, UTCMillisDate v);

  MutableSonMap put(String name, short v);

  MutableSonMap put(String name, int v);

  MutableSonMap put(String name, char v);

  MutableSonMap put(String name, long v);

  MutableSonMap put(String name, float v);

  MutableSonMap put(String name, double v);

  default MutableSonMap put(String name, byte signifier, byte[] b) {
    return put(name, signifier, ByteBuffer.wrap(b));
  }

  MutableSonMap put(String name, byte signifier, ByteBuffer buf);

  default MutableSonMap put(String name, byte signifier, byte[] b, int off, int len) {
    return put(name, signifier, ByteBuffer.wrap(b, off, len));
  }

  MutableSonMap put(String name, SonBytes sb);

  MutableSonMap put(String name, String value);

  MutableSonMap put(String name, MutableSonMap map);

  MutableSonMap put(String name, MutableSonList ml);

  MutableSonMap put(String name, UUID uuid);

  default MutableSonMap put(String name, SonType type, Object value) {
    return put(new MutableSonValue.MapValue(name, type, value));
  }

  MutableSonMap put(MutableSonValue.MapValue mv);

  MutableSonMap putNull(String name);

  MutableSonMap remove(String name);

  default ByteBuffer toBuffer() {
    return toBuffer((NameSource) null);
  }

  ByteBuffer toBuffer(NameSource ns);

  default void toBuffer(ManagedBuffer dest) {
    toBuffer(null, dest);
  }

  void toBuffer(NameSource ns, ManagedBuffer dest);
}
