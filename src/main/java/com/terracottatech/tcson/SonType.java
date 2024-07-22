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

import com.terracottatech.tcson.mutable.MutableSonListImpl;
import com.terracottatech.tcson.mutable.MutableSonMapImpl;
import com.terracottatech.tcson.writing.SonStreamingListWriter;
import com.terracottatech.tcson.writing.SonStreamingMapWriter;

import java.util.Objects;
import java.util.UUID;

/**
 * Son types. Son values are strongly typed, per this enum.
 */
public enum SonType {

  BYTE(Byte.class, false) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.append(name, (byte) value);
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.append((byte) value);
    }
  },
  SHORT(Short.class, false) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.append(name, (short) value);
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.append((short) value);
    }
  },
  INT(Integer.class, false) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.append(name, (int) value);
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.append((int) value);
    }
  },
  CHAR(Character.class, false) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.append(name, (char) value);
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.append((char) value);
    }
  },
  LONG(Long.class, true) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.append(name, (long) value);
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.append((long) value);
    }
  },
  FLOAT(Float.class, false) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.append(name, (float) value);
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.append((float) value);
    }
  },
  DOUBLE(Double.class, true) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.append(name, (double) value);
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.append((double) value);
    }
  },
  STRING(String.class, true) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.append(name, (String) value);
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.append((String) value);
    }
  },
  BOOL(Boolean.class, true) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.append(name, (boolean) value);
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.append((boolean) value);
    }
  },
  MAP(SonMap.class, true) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      SonStreamingMapWriter<?> innerMap = map.map(name);
      ((MutableSonMapImpl) value).appendTo(innerMap);
      innerMap.endMap();
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      SonStreamingMapWriter<?> innerMap = list.map();
      ((MutableSonMapImpl) value).appendTo(innerMap);
      innerMap.endMap();
    }
  },
  LIST(SonList.class, true) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      SonStreamingListWriter<?> innerList = map.list(name);
      ((MutableSonListImpl) value).appendTo(innerList);
      innerList.endList();
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      SonStreamingListWriter<?> innerList = list.list();
      ((MutableSonListImpl) value).appendTo(innerList);
      innerList.endList();
    }
  },
  BYTES(SonBytes.class, false) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      SonBytes sb = (SonBytes) value;
      map.append(name, sb.getSignifier(), sb.getBuffer().slice());
    }

    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      SonBytes sb = (SonBytes) value;
      list.append(sb.getSignifier(), sb.getBuffer().slice());
    }
  },
  DATE(UTCMillisDate.class, false) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.append(name, (UTCMillisDate) value);
    }

    @Override
    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.append((UTCMillisDate) value);
    }
  },
  NULL(Void.class, true) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      map.appendNull(name);
    }

    @Override
    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      list.appendNull();
    }
  },
  UUID(java.util.UUID.class, false) {
    @Override
    public void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value) {
      UUID uuid = (java.util.UUID) value;
      map.append(name, uuid);
    }

    @Override
    public void mutableListToBuffered(SonStreamingListWriter<?> list, Object value) {
      UUID uuid = (java.util.UUID) value;
      list.append(uuid);
    }
  };

  static boolean TYPE_CHECKING = true;
  private final Class<?> clz;
  private final boolean strictJSON;

  SonType(Class<?> clz, boolean json) {
    this.clz = clz;
    strictJSON = json;
  }

  public static void main(String[] args) {
    System.out.println(SonType.values().length);
  }

  public void checkType(Object value) {
    if (this.equals(NULL)) {
      if (value != null) {
        throw new IllegalArgumentException("Non null value for null seen");
      }
    } else {
      Objects.requireNonNull(value);
      if (TYPE_CHECKING) {
        if (clz != null && !clz.isAssignableFrom(value.getClass())) {
          throw new IllegalArgumentException("Expected: " + clz + " got " + value.getClass());
        }
      }
    }
  }

  public boolean isStrictlyJSON() {
    return strictJSON;
  }

  public abstract void mutableListToBuffered(SonStreamingListWriter<?> list, Object value);

  public abstract void mutableMapToBuffered(SonStreamingMapWriter<?> map, String name, Object value);

  public Object dupValue(Object in) {
    switch (this) {
      case MAP:
        SonMap<?> m = (SonMap<?>) in;
        return m.deepCopy();
      case LIST:
        SonList<?> l = (SonList<?>) in;
        return l.deepCopy();
      case BYTES:
        SonBytes sb = (SonBytes) in;
        return sb.dup();
      case CHAR:
      case BOOL:
      case LONG:
      case INT:
      case DOUBLE:
      case STRING:
      case UUID:
      case BYTE:
      case DATE:
      case FLOAT:
      case SHORT:
      case NULL:
        return in;
      default:
        throw new IllegalStateException();
    }
  }
}
