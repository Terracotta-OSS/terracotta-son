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
package com.terracottatech.tcson;

import java.util.Objects;
import java.util.UUID;

/**
 * A strongly typed value, used in a SonLists.
 */
public abstract class SonValue implements Comparable<SonValue> {

  protected final SonType type;
  protected final Object value;

  /**
   * Instantiates a new Son value.
   *
   * @param type the type
   * @param value the value
   */
  public SonValue(SonType type, Object value) {
    type.checkType(value);
    this.type = type;
    this.value = value;
  }

  /**
   * Get boolean value.
   *
   * @return the boolean
   * @throws ClassCastException if not a boolean.
   */
  public boolean boolValue() {
    checkType(SonType.BOOL);
    return (boolean) value;
  }

  /**
   * Get value as byte.
   *
   * @return the byte
   * @throws ClassCastException if not a byte.
   */
  public byte byteValue() {
    checkType(SonType.BYTE);
    return (byte) value;
  }

  protected void checkType(SonType... types) {
    for (SonType t : types) {
      if (type == t) {
        return;
      }
    }
    throw new ClassCastException();
  }

  /**
   * Get byte[] value.
   *
   * @return the byte [ ]
   * @throws ClassCastException if not a byte[].
   */
  public SonBytes bytesValue() {
    checkType(SonType.BYTES);
    return (SonBytes) value;
  }

  /**
   * Get char value
   *
   * @return the char
   * @throws ClassCastException if not a char.
   */
  public char charValue() {
    checkType(SonType.CHAR);
    return (char) value;
  }

  public UTCMillisDate dateValue() {
    checkType(SonType.DATE);
    return (UTCMillisDate) value;
  }

  /**
   * Get double value
   *
   * @return the double
   * @throws ClassCastException if not a double.
   */
  public double doubleValue() {
    checkType(SonType.DOUBLE, SonType.FLOAT);
    return (double) value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (o instanceof SonValue) {
      SonValue value1 = (SonValue) o;
      if (type != value1.type) {
        return false;
      }
      switch (type) {
        case MAP:
          return mapValue().deepEquals(value1.mapValue());
        case LIST:
          return listValue().deepEquals(value1.listValue());
        case NULL:
          return true;
        default:
          return Objects.equals(value, value1.getValue());
      }
    }
    return false;
  }

  /**
   * Get the value
   *
   * @return the value
   */
  public Object getValue() {
    return value;
  }

  /**
   * Get a SonMap value.
   *
   * @return the map
   * @throws ClassCastException if not a map.
   */
  public abstract SonMap<?> mapValue();

  /**
   * Get a SonList value.
   *
   * @return the t
   * @throws ClassCastException if not a list.
   */
  public abstract SonList<?> listValue();

  /**
   * Get float value.
   *
   * @return the float
   * @throws ClassCastException if not a float.
   */
  public float floatValue() {
    checkType(SonType.FLOAT);
    return (float) value;
  }

  /**
   * Gets type of value.
   *
   * @return the type
   */
  public SonType getType() {
    return type;
  }

  @Override
  public int hashCode() {
    switch (type) {
      case MAP:
        return mapValue().deepHashCode();
      case LIST:
        return listValue().deepHashCode();
      default:
        return Objects.hash(type, value);
    }
  }

  /**
   * Get int value
   *
   * @return the int
   * @throws ClassCastException if not a int.
   */
  public int intValue() {
    checkType(SonType.INT, SonType.SHORT, SonType.BYTE);
    return (int) value;
  }

  /**
   * Is this the null value?
   *
   * @return true if null
   */
  public boolean isNullValue() {
    return this.type.equals(SonType.NULL);
  }

  /**
   * Get long value
   *
   * @return the long
   * @throws ClassCastException if not a long.
   */
  public long longValue() {
    checkType(SonType.LONG, SonType.INT, SonType.SHORT, SonType.BYTE);
    return (long) value;
  }

  public Number numberValue() {
    checkType(SonType.FLOAT, SonType.DOUBLE, SonType.LONG, SonType.INT, SonType.SHORT, SonType.BYTE);
    return (Number) value;
  }

  /**
   * Get value as short
   *
   * @return the short
   * @throws ClassCastException if not a short.
   */
  public short shortValue() {
    checkType(SonType.SHORT, SonType.BYTE);
    return (short) value;
  }

  /**
   * Get String value.
   *
   * @return the string
   * @throws ClassCastException if not a String.
   */
  public String stringValue() {
    checkType(SonType.STRING);
    return (String) value;
  }

  @Override
  public String toString() {
    return "SonListValue{" + "type=" + type + ", value=" + value + '}';
  }

  /**
   * Return UUID value.
   *
   * @return uuid
   */
  public UUID uuidValue() {
    return (UUID) value;
  }

  @Override
  @SuppressWarnings( { "raw", "unchecked" })
  public int compareTo(SonValue o) {
    int ret = getType().compareTo(o.getType());
    if (ret == 0) {
      // janky, but I know they are all comparable.
      // something to muse on,that value should be comparable.
      ret = ((Comparable) value).compareTo(o.value);
    }
    return ret;
  }
}
