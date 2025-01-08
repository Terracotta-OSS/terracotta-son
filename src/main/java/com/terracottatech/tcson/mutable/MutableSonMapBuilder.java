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

public class MutableSonMapBuilder {
  private final MutableSonMap map;

  public MutableSonMapBuilder(MutableSonMap map) {
    this.map = map;
  }

  public MutableSonMapBuilder() {
    this.map = new MutableSonMapImpl();
  }

  public MutableSonMap get() {
    return map;
  }

  public MutableSonMapBuilder put(String key, byte v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, short v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, int v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, long v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, float v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, double v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, char v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, SonBytes bytes) {
    map.put(key, bytes);
    return this;
  }

  public MutableSonMapBuilder put(String key, boolean v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, String v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, MutableSonList v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, MutableSonMap v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, UTCMillisDate v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder put(String key, UUID v) {
    map.put(key, v);
    return this;
  }

  public MutableSonMapBuilder putNull(String key) {
    map.putNull(key);
    return this;
  }
}
