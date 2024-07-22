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

import com.terracottatech.tcson.mutable.MutableSonListBuilder;
import com.terracottatech.tcson.mutable.MutableSonValue;
import com.terracottatech.tcson.pile.ManagedBuffer;

import java.io.Externalizable;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.UUID;

/**
 * A Mutable SonList, allowing for general mutation of
 * strongly typed values in a SonList.
 */
public interface MutableSonList extends SonList<MutableSonValue>, Externalizable {
  long serialVersionUID = 42L;

  default MutableSonList add(SonType type, Object value) {
    return set(size(), type, value);
  }

  MutableSonList set(int idx, SonType type, Object value);

  default MutableSonList add(boolean v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, boolean v);

  default MutableSonList add(char v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, char v);

  default MutableSonList add(String v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, String v);

  default MutableSonList add(MutableSonValue value) {
    return set(size(), value);
  }

  MutableSonList set(int idx, MutableSonValue value);

  default MutableSonList add(UTCMillisDate v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, UTCMillisDate v);

  default MutableSonList add(UUID v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, UUID v);

  default MutableSonList add(byte v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, byte v);

  default MutableSonList add(short v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, short v);

  default MutableSonList add(float v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, float v);

  default MutableSonList add(int v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, int v);

  default MutableSonList add(double v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, double v);

  default MutableSonList add(long v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, long v);

  default MutableSonList add(MutableSonList v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, MutableSonList v);

  default MutableSonList add(MutableSonMap v) {
    return set(size(), v);
  }

  MutableSonList set(int idx, MutableSonMap v);

  MutableSonList add(int idx, SonType type, Object value);

  MutableSonList add(int idx, boolean v);

  MutableSonList add(int idx, UTCMillisDate v);

  MutableSonList add(int idx, UUID v);

  MutableSonList add(int idx, char v);

  MutableSonList add(int idx, MutableSonValue value);

  MutableSonList add(int idx, String v);

  MutableSonList add(int idx, byte v);

  MutableSonList add(int idx, short v);

  MutableSonList add(int idx, int v);

  MutableSonList add(int idx, long v);

  MutableSonList add(int idx, float v);

  MutableSonList add(int idx, double v);

  MutableSonList add(int idx, MutableSonList v);

  MutableSonList add(int idx, MutableSonMap v);

  default MutableSonList add(int idx, byte signifier, byte[] v) {
    return add(idx, new SonBytes(signifier, ByteBuffer.wrap(v)));
  }

  MutableSonList add(int idx, SonBytes sb);

  default MutableSonList add(int idx, byte signifier, byte[] v, int off, int len) {
    return add(idx, new SonBytes(signifier, ByteBuffer.wrap(v, off, len)));
  }

  default MutableSonList add(int idx, byte signifier, ByteBuffer buf) {
    return add(idx, new SonBytes(signifier, buf));
  }

  default MutableSonList add(byte signifier, byte[] v) {
    return set(size(), signifier, v);
  }

  default MutableSonList set(int idx, byte signifier, byte[] v) {
    return set(idx, new SonBytes(signifier, ByteBuffer.wrap(v)));
  }

  default MutableSonList add(byte signifier, byte[] v, int off, int len) {
    return set(size(), signifier, v, off, len);
  }

  default MutableSonList set(int idx, byte signifier, byte[] v, int off, int len) {
    return set(idx, new SonBytes(signifier, ByteBuffer.wrap(v, off, len)));
  }

  default MutableSonList addNull() {
    return setNull(size());
  }

  MutableSonList setNull(int idx);

  MutableSonList addNull(int idx);

  @Override
  default MutableSonList asMutable() {
    return this;
  }

  @Override
  MutableSonValue asSonValue();

  default MutableSonListBuilder builder() {
    return new MutableSonListBuilder(this);
  }

  MutableSonList clear();

  MutableSonList remove(int idx);

  default MutableSonList set(int idx, byte signifier, ByteBuffer buf) {
    return set(idx, new SonBytes(signifier, buf));
  }

  MutableSonList set(int idx, SonBytes sb);

  void sort(Comparator<? super SonValue> comparator);

  default ByteBuffer toBuffer() {
    return toBuffer((NameSource) null);
  }

  ByteBuffer toBuffer(NameSource ns);

  default void toBuffer(ManagedBuffer dest) {
    toBuffer(null, dest);
  }

  void toBuffer(NameSource ns, ManagedBuffer dest);
}
