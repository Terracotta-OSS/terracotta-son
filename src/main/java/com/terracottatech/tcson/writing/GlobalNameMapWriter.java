/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.writing;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Global map for recording names::ids. Note that ids are recorded in the order
 * they are allocated, so the first is 0, second is 1, etc. This is *important* as
 * it allows us to not acually store the ids.
 */
public class GlobalNameMapWriter {
  private final LinkedHashMap<String, Integer> map;
  private int idGen = 0;

  public GlobalNameMapWriter() {
    map = new LinkedHashMap<>();
  }

  public int allocateId(String name) {
    Integer p = map.get(name);
    if (p != null) {
      return p;
    }
    int id = idGen++;
    map.put(name, id);
    return id;
  }

  public void clear() {
    map.clear();
    idGen = 0;
  }

  public Set<String> getNamesInOrder() {
    return map.keySet();
  }

  public int size() {
    return map.size();
  }

}
