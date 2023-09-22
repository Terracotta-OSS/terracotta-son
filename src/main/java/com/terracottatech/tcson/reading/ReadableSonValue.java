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
package com.terracottatech.tcson.reading;

import com.terracottatech.tcson.ReadableSonList;
import com.terracottatech.tcson.ReadableSonMap;
import com.terracottatech.tcson.SonBytes;
import com.terracottatech.tcson.SonMapValue;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.SonValue;
import com.terracottatech.tcson.UTCMillisDate;
import com.terracottatech.tcson.writing.SonWriter;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ReadableSonValue extends SonValue {

  public static final class MapValue extends SonMapValue {
    public MapValue(String key, SonType type, Object value) {
      super(key, type, value);
    }

    public ReadableSonList listValue() {
      checkType(SonType.LIST);
      return (ReadableSonList) value;
    }

    public ReadableSonMap mapValue() {
      checkType(SonType.MAP);
      return (ReadableSonMap) value;
    }
  }
  public static ReadableSonValue NULL_VALUE = new ReadableSonValue(SonType.NULL, null);

  public ReadableSonValue(SonType type, Object value) {
    super(type, value);
  }

  static SonType typeFromSignifier(byte sig) {
    switch (sig) {
      case SonWriter.DATE_SIGNIFIER:
        return SonType.DATE;
      case SonWriter.UUID_SIGNIFIER:
        return SonType.UUID;
      default:
        return SonType.BYTES;
    }
  }

  static Object formFromBytes(byte sig, ByteBuffer bb) {
    switch (sig) {
      case SonWriter.DATE_SIGNIFIER:
        return new UTCMillisDate(bb.getLong());
      case SonWriter.UUID_SIGNIFIER:
        return new UUID(bb.getLong(), bb.getLong());
      default:
        return new SonBytes(sig, bb);
    }
  }

  public ReadableSonList listValue() {
    checkType(SonType.LIST);
    return (ReadableSonList) value;
  }

  public ReadableSonMap mapValue() {
    checkType(SonType.MAP);
    return (ReadableSonMap) value;
  }

}
