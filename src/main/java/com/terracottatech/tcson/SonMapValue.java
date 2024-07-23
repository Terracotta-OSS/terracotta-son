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
