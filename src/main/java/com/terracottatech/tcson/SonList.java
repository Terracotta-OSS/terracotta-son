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
import com.terracottatech.tcson.mutable.MutableSonValue;

/**
 * A SonList is a readable, iterable list of SonValue objects.
 */
public interface SonList<E extends SonValue> extends Iterable<E> {
  static MutableSonList writeable() {
    return new MutableSonListImpl();
  }

  /**
   * Turn a ReadableSonList into a mutable one.
   *
   * @return the mutable son list
   */
  MutableSonList asMutable();

  default MutableSonList deepCopy() {
    MutableSonList list = Son.writeableList();
    for (int i = 0; i < this.size(); i++) {
      E val = this.get(i);
      list.add(i, new MutableSonValue(val.getType(), val.getType().dupValue(val.getValue())));
    }
    return list;
  }

  SonValue asSonValue();

  default boolean deepEquals(SonList<?> other) {
    if (other == null) {
      return false;
    }
    if (size() != other.size()) {
      return false;
    }
    for (int i = 0; i < size(); i++) {
      SonValue me = get(i);
      SonValue them = other.get(i);
      if (!me.equals(them)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Number of elements in this list.
   *
   * @return size
   */
  int size();

  /**
   * Get the SonValue at this index.
   *
   * @param idx the index
   * @return the SonValue
   */
  E get(int idx);

  default int deepHashCode() {
    int hs = 0;
    for (E ent : this) {
      hs = hs + ent.hashCode();
    }
    return hs;
  }

  /**
   * Optional traversal view.
   *
   * @return list as opt
   */
  default OptSonList opt() {
    return new OptSonList(this);
  }

}
