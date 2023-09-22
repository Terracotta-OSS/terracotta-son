/*
 * Copyright (c) 2020-2023 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
