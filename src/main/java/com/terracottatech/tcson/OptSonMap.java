/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Class for referencing your way down a SonMap structure. At each point,
 * you either get a typed Optional (if you ask a doc for primitive
 * element like a boolean) or another List/Map.
 * structure.
 */
public class OptSonMap {
  static final OptSonMap EMPTY = new OptSonMap();
  private final SonMap<?> map;

  private OptSonMap() {
    map = null;
  }

  public OptSonMap(SonMap<?> map) {
    Objects.requireNonNull(map);
    this.map = map;
  }

  @SuppressWarnings("unchecked")
  public Optional<Boolean> boolValue(String key) {
    return (Optional<Boolean>) optional(key, SonType.BOOL);
  }

  private Optional<?> optional(String key, SonType t) {
    if (map != null) {
      SonValue p = map.get(key);
      if (p != null && p.getType() == t) {
        return Optional.of(p.value); // TODO use enum to do this?
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public Optional<SonBytes> bytesValue(String key) {
    return (Optional<SonBytes>) optional(key, SonType.BYTES);
  }

  @SuppressWarnings("unchecked")
  public Optional<Character> charValue(String key) {
    return (Optional<Character>) optional(key, SonType.CHAR);
  }

  @SuppressWarnings("unchecked")
  public Optional<Double> doubleValue(String key) {
    return (Optional<Double>) optional(key, SonType.DOUBLE);
  }

  @SuppressWarnings("unchecked")
  public Optional<Integer> intValue(String key) {
    return (Optional<Integer>) optional(key, SonType.INT);
  }

  public OptSonList listValue(String key) {
    if (map != null) {
      SonValue p = map.get(key);
      if (p != null) {
        if (p.getType() == SonType.LIST) {
          return new OptSonList(p.listValue());
        }
      }
    }
    return OptSonList.EMPTY;
  }

  @SuppressWarnings("unchecked")
  public Optional<Long> longValue(String key) {
    return (Optional<Long>) optional(key, SonType.LONG);
  }

  public OptSonMap mapValue(String key) {
    if (map != null) {
      SonValue p = map.get(key);
      if (p != null) {
        if (p.getType() == SonType.MAP) {
          return new OptSonMap(p.mapValue());
        }
      }
    }
    return EMPTY;
  }

  public Optional<SonMap<?>> mapValue() {
    return Optional.ofNullable(map);
  }

  public Optional<Boolean> nullValue(String key) {
    if (map != null && map.get(key) != null && map.get(key).getType() == SonType.NULL) {
      return Optional.of(true);
    }
    return Optional.empty();
  }

  public Optional<Number> numberValue(String key) {
    if (map != null) {
      SonValue p = map.get(key);
      if (p != null) {
        switch (p.getType()) {
          case DOUBLE:
            return Optional.of(p.doubleValue());
          case INT:
            return Optional.of(p.intValue());
          case SHORT:
            return Optional.of(p.shortValue());
          case BYTE:
            return Optional.of(p.byteValue());
          case LONG:
            return Optional.of(p.longValue());
          default:
            break;
        }
      }
    }
    return Optional.empty();
  }

  public Optional<Integer> size() {
    if (map == null) {
      return Optional.empty();
    }
    return Optional.of(map.size());
  }

  @SuppressWarnings("unchecked")
  public Optional<String> stringValue(String key) {
    return (Optional<String>) optional(key, SonType.STRING);
  }

  @SuppressWarnings("unchecked")
  public Optional<UTCMillisDate> utcValue(String key) {
    return (Optional<UTCMillisDate>) optional(key, SonType.DATE);
  }

  @SuppressWarnings("unchecked")
  public Optional<UUID> uuidValue(String key) {
    return (Optional<UUID>) optional(key, SonType.UUID);
  }

}
