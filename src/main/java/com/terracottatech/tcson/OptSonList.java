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

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Class for referencing your way down a SonList structure. At each point,
 * you either get a typed Optional (if you ask a doc for primitive
 * element like a boolean) or another Map/List.
 */
public class OptSonList {
  static final OptSonList EMPTY = new OptSonList();
  private final SonList<?> list;

  private OptSonList() {
    this.list = null;
  }

  public OptSonList(SonList<?> list) {
    this.list = list;
    Objects.requireNonNull(list);
  }

  @SuppressWarnings("unchecked")
  public Optional<Boolean> boolValue(int index) {
    return (Optional<Boolean>) optional(index, SonType.BOOL);
  }

  private Optional<?> optional(int index, SonType t) {
    if (list != null && index < list.size()) {
      SonValue p = list.get(index);
      if (p.getType() == t) {
        return Optional.of(p.value);
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public Optional<Integer> byteValue(int index) {
    return (Optional<Integer>) optional(index, SonType.BYTE);
  }

  @SuppressWarnings("unchecked")
  public Optional<SonBytes> bytesValue(int index) {
    return (Optional<SonBytes>) optional(index, SonType.BYTES);
  }

  @SuppressWarnings("unchecked")
  public Optional<Character> charValue(int index) {
    return (Optional<Character>) optional(index, SonType.CHAR);
  }

  @SuppressWarnings("unchecked")
  public Optional<Double> doubleValue(int index) {
    return (Optional<Double>) optional(index, SonType.DOUBLE);
  }

  @SuppressWarnings("unchecked")
  public Optional<Integer> intValue(int index) {
    return (Optional<Integer>) optional(index, SonType.INT);
  }

  public Optional<SonList<?>> listValue() {
    return Optional.ofNullable(list);
  }

  public OptSonList listValue(int index) {
    if (list != null && index < list.size()) {
      SonValue p = list.get(index);
      if (p.getType() == SonType.LIST) {
        return new OptSonList(p.listValue());
      }
    }
    return EMPTY;
  }

  @SuppressWarnings("unchecked")
  public Optional<Long> longValue(int index) {
    return (Optional<Long>) optional(index, SonType.LONG);
  }

  public OptSonMap mapValue(int index) {
    if (list != null && index < list.size()) {
      SonValue p = list.get(index);
      if (p.getType() == SonType.MAP) {
        return new OptSonMap(p.mapValue());
      }
    }
    return OptSonMap.EMPTY;
  }

  public Optional<Boolean> nullValue(int index) {
    if (list != null && index < list.size() && list.get(index).getType() == SonType.NULL) {
      return Optional.of(true);
    }
    return Optional.empty();
  }

  public Optional<Number> numberValue(int index) {
    if (list != null && index < list.size()) {

      SonValue p = list.get(index);
      switch (p.getType()) {
        case SHORT:
          return Optional.of(p.shortValue());
        case BYTE:
          return Optional.of(p.byteValue());
        case DOUBLE:
          return Optional.of(p.doubleValue());
        case INT:
          return Optional.of(p.intValue());
        case LONG:
          return Optional.of(p.longValue());
        default:
          break;
      }
    }

    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public Optional<Integer> shortValue(int index) {
    return (Optional<Integer>) optional(index, SonType.SHORT);
  }

  public Optional<Integer> size() {
    if (list == null) {
      return Optional.empty();
    }
    return Optional.of(list.size());
  }

  @SuppressWarnings("unchecked")
  public Optional<String> stringValue(int index) {
    return (Optional<String>) optional(index, SonType.STRING);
  }

  @SuppressWarnings("unchecked")
  public Optional<UTCMillisDate> utcValue(int index) {
    return (Optional<UTCMillisDate>) optional(index, SonType.DATE);
  }

  @SuppressWarnings("unchecked")
  public Optional<UUID> uuidValue(int index) {
    return (Optional<UUID>) optional(index, SonType.UUID);
  }

}
