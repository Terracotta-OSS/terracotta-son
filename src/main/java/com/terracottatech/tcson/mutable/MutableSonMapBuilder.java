/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
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
