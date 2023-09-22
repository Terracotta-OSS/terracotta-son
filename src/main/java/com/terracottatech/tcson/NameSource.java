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
package com.terracottatech.tcson;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public interface NameSource {
  class Naive implements NameSource {
    private final int max;
    ConcurrentHashMap<String, Long> nameToId = new ConcurrentHashMap<>();
    ConcurrentHashMap<Long, String> idToName = new ConcurrentHashMap<>();
    private AtomicLong idgen = new AtomicLong();

    public Naive(int max) {
      this.max = max;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Naive naive = (Naive) o;
      return max == naive.max &&
             idgen.equals(naive.idgen) &&
             nameToId.equals(naive.nameToId) &&
             idToName.equals(naive.idToName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(max, idgen, nameToId, idToName);
    }

    @Override
    public Long idOf(String name) {
      Long ret = nameToId.get(name);
      if (ret == null && nameToId.size() < max) {
        long gid = idgen.incrementAndGet();
        Long p = nameToId.putIfAbsent(name, gid);
        if (p == null) {
          if (idToName.putIfAbsent(gid, name) == null) {
            return gid;
          } else {
            throw new IllegalStateException();
          }
        }
        ret = p;
      }
      return ret;
    }

    @Override
    public String nameOf(long id) {
      return idToName.get(id);
    }
  }

  Long idOf(String name);

  String nameOf(long id);
}
