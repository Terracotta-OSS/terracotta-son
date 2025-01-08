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
package com.terracottatech.tcson.mutable;

import com.terracottatech.tcson.MutableSonList;
import com.terracottatech.tcson.MutableSonMap;
import com.terracottatech.tcson.SonBytes;
import com.terracottatech.tcson.UTCMillisDate;

import java.util.UUID;

public class MutableSonListBuilder {
  private final MutableSonList list;

  public MutableSonListBuilder(MutableSonList list) {
    this.list = list;
  }

  public MutableSonListBuilder() {
    this.list = new MutableSonListImpl();
  }

  public MutableSonListBuilder add(boolean v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, boolean v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(char v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, char v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(byte v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, byte v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(short v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, short v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(int v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, int v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(long v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, long v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(float v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, float v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(double v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, double v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(String v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, String v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(SonBytes v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, SonBytes v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(UTCMillisDate v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, UTCMillisDate v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(MutableSonList v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, MutableSonList v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(MutableSonMap v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, MutableSonMap v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder add(UUID v) {
    return add(list.size(), v);
  }

  public MutableSonListBuilder add(int idx, UUID v) {
    list.add(idx, v);
    return this;
  }

  public MutableSonListBuilder addNull() {
    list.addNull();
    return this;
  }

  public MutableSonListBuilder addNull(int idx) {
    list.addNull(idx);
    return this;
  }

  public MutableSonList get() {
    return list;
  }

  public MutableSonListBuilder set(int idx, boolean v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, char v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, byte v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, short v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, int v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, long v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, float v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, double v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, String v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, SonBytes v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, UTCMillisDate v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, MutableSonList v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, MutableSonMap v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder set(int idx, UUID v) {
    list.set(idx, v);
    return this;
  }

  public MutableSonListBuilder setNull(int idx) {
    list.setNull(idx);
    return this;
  }
}
