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
package com.terracottatech.tcson.parser;

import com.terracottatech.tcson.SonList;
import com.terracottatech.tcson.SonValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FieldReference {
  public static class MapReference {
    private final String mapField;

    public MapReference(String mapField) {
      this.mapField = mapField;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      MapReference reference = (MapReference) o;
      return Objects.equals(mapField, reference.mapField);
    }

    public String getFieldName() {
      return mapField;
    }

    @Override
    public int hashCode() {
      return Objects.hash(mapField);
    }

    @Override
    public String toString() {
      return "MapReference{" + mapField + '}';
    }
  }

  public static class ArraySlice {
    private int left;
    private int right;

    public ArraySlice(int one) {
      this(one, one);
    }

    public List<Integer> extractPositions(int listSize) {
      HashSet<Integer> set = new HashSet<Integer>();
      int actualLeft = actual(left, listSize);
      int actualRight = actual(right, listSize);
      for (int i = actualLeft; i <= actualRight && i < listSize; i++) {
        if (i >= 0) {
          set.add(i);
        }
      }
      ArrayList<Integer> ret = new ArrayList<>(set);
      ret.sort(null);
      return ret;
    }

    public List<SonValue> extract(SonList<?> l) {
      LinkedList<SonValue> vals = new LinkedList<>();
      List<Integer> positions = extractPositions(l.size());
      for (int i : positions) {
        vals.add(l.get(i));
      }
      return vals;
    }

    private int actual(int pos, int size) {
      if (pos < 0) {
        pos = size + pos;
      }
      if (pos < 0) {
        return -1;
      }
      pos = Math.min(pos, size);
      return pos;
    }

    public int getLeft() {
      return left;
    }

    public int getRight() {
      return right;
    }

    public ArraySlice(int left, int right) {
      this.left = left;
      this.right = right;
    }

    @Override
    public String toString() {
      return left + ":" + right;
    }
  }

  public static class ArraySpec {
    private final List<ArraySlice> arraySpec;
    private final String name;

    public ArraySpec(List<ArraySlice> arraySpec) {
      ArrayList<ArraySlice> mine = new ArrayList<>(arraySpec);
      this.arraySpec = Collections.unmodifiableList(mine);
      this.name = stringVariant();
    }

    private String stringVariant() {
      StringBuilder sb = new StringBuilder();
      boolean needsComma = false;
      sb.append('[');
      for (ArraySlice al : arraySpec) {
        if (needsComma) {
          sb.append(", ");
        } else {
          needsComma = true;
        }
        sb.append(al);
      }
      sb.append(']');

      return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ArraySpec spec = (ArraySpec) o;
      return Objects.equals(arraySpec, spec.arraySpec) && Objects.equals(name, spec.name);
    }

    public String getName() {
      return name;
    }

    @Override
    public int hashCode() {
      return Objects.hash(arraySpec);
    }

    @Override
    public String toString() {
      return "ArraySpec{" + name + '}';
    }

    public List<ArraySlice> getArrayMembers() {
      return arraySpec;
    }
  }

  private final Type type;
  private final ArraySpec arraySpec;
  private final MapReference mapRef;

  public enum Type {
    MAP,
    ARRAY,
    WILD
  }

  public FieldReference(List<ArraySlice> arraySpec) {
    this.arraySpec = new ArraySpec(arraySpec);
    this.type = Type.ARRAY;
    this.mapRef = null;
  }

  public FieldReference() {
    this.type = Type.WILD;
    this.mapRef = null;
    this.arraySpec = null;
  }

  public FieldReference(String mapField) {
    this.mapRef = new MapReference(mapField);
    this.type = Type.MAP;
    this.arraySpec = null;
  }

  public ArraySpec arrSpec() {
    if (type == Type.ARRAY) {
      return arraySpec;
    }
    throw new ClassCastException();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FieldReference reference = (FieldReference) o;
    return type == reference.type &&
           Objects.equals(arraySpec, reference.arraySpec) &&
           Objects.equals(mapRef, reference.mapRef);
  }

  public Type getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, arraySpec, mapRef);
  }

  public boolean isWild() {
    return getType().equals(Type.WILD);
  }

  public MapReference mapRef() {
    if (type == Type.MAP) {
      return mapRef;
    }
    throw new ClassCastException();
  }

  @Override
  public String toString() {
    switch (getType()) {
      case WILD:
        return "[]";
      case ARRAY:
        return arraySpec.toString();
      case MAP:
        return mapRef.toString();
      default:
        throw new IllegalStateException();
    }
  }
}
