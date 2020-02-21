/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson;

import com.terracottatech.tcson.mutable.MutableSonValue;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A SonMap is a map of String keys to strongly typed SonMapValue objects.
 * This interface specifies a readable, iterable map who size can be checked.
 *
 * @param <V> the type parameter
 */
public interface SonMap<V extends SonMapValue> extends Iterable<V> {

  /**
   * Turn into a mutable SonMap.
   *
   * @return the mutable son map
   */
  MutableSonMap asMutable();

  default MutableSonMap deepCopy() {
    MutableSonMap map = Son.writeableMap();
    for(V val:this) {
      map.put(val.getKey(), val.getType(), val.getType().dupValue(val.getValue()));
    }
    return map;
  }

  SonValue asSonValue();

  default boolean deepEquals(SonMap<?> other) {
    if (other == null) {
      return false;
    }
    if (size() != other.size()) {
      return false;
    }
    for (SonMapValue me : this) {
      SonMapValue them = other.get(me.getKey());
      if (!me.equals(them)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Get the SonMapValue for this key.
   *
   * @param name the key name
   * @return the map value, or null if none.
   */
  V get(String name);

  default int deepHashCode() {
    int hs = 0;
    for (V ent : this) {
      hs = hs + ent.hashCode();
    }
    return hs;
  }

  /**
   * Is this map empty.
   *
   * @return true if empty.
   */
  default boolean isEmpty() {
    return size() == 0;
  }

  /**
   * Number of elements in this map.
   *
   * @return size
   */
  int size();

  default Iterable<String> keys() {
    final Iterator<V> base = this.iterator();
    Iterator<String> iter = new Iterator<String>() {
      @Override
      public boolean hasNext() {
        return base.hasNext();
      }

      @Override
      public String next() {
        if (base.hasNext()) {
          return base.next().getKey();
        }
        throw new NoSuchElementException();
      }
    };
    return () -> iter;
  }

  /**
   * Optional traversal view.
   */
  default OptSonMap opt() {
    return new OptSonMap(this);
  }
}