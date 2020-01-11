/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson;

import com.terracottatech.tcson.mutable.MutableSonListImpl;

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
   * @return
   */
  default OptSonList opt() {
    return new OptSonList(this);
  }

}
