/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
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
