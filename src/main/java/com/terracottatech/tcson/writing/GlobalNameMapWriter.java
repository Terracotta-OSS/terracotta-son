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
