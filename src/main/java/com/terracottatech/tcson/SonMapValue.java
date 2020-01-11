/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson;

import java.util.Objects;

/**
 * The strongly typed value for a SonMap. Exactly like a
 * <p>
 * SonValue except it also includes the key.
 */
public abstract class SonMapValue extends SonValue {
  private final String key;

  public SonMapValue(String key, SonType type, Object value) {
    super(type, value);
    Objects.requireNonNull(key);
    this.key = key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !(o instanceof SonMapValue)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    SonMapValue value = (SonMapValue) o;
    return key.equals(value.key);
  }

  /**
   * Get the key for this value.
   *
   * @return the key
   */
  public String getKey() {
    return key;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), key);
  }

  @Override
  public String toString() {
    return "SonMapValue{" + "key='" + key + '\'' + ", type=" + type + ", value=" + value + '}';
  }
}
