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
