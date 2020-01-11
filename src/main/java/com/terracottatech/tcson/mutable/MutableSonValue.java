/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.mutable;

import com.terracottatech.tcson.MutableSonList;
import com.terracottatech.tcson.MutableSonMap;
import com.terracottatech.tcson.SonMapValue;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.SonValue;

public class MutableSonValue extends SonValue {

  public static final class MapValue extends SonMapValue {

    public MapValue(String key, SonType type, Object value) {
      super(key, type, value);
    }

    public MutableSonList listValue() {
      checkType(SonType.LIST);
      return (MutableSonList) value;
    }

    public MutableSonMap mapValue() {
      checkType(SonType.MAP);
      return (MutableSonMap) value;
    }
  }

  public static final MutableSonValue NULL_VALUE = new MutableSonValue(SonType.NULL, null);

  public MutableSonValue(SonType type, Object value) {
    super(type, value);
  }

  public MutableSonList listValue() {
    checkType(SonType.LIST);
    return (MutableSonList) value;
  }

  public MutableSonMap mapValue() {
    checkType(SonType.MAP);
    return (MutableSonMap) value;
  }
}
