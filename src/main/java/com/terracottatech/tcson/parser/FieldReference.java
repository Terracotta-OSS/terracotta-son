/*
 * Copyright (c) 2011-2018 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FieldReference {
  public static class MapReference {
    private final String mapField;

    public MapReference(String mapField) {
      this.mapField = mapField;
    }

    public MapReference() {
      this.mapField = null;
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
      if (isWildcard()) {
        return "MapReference-wildcard";
      }
      return "MapReference{" + mapField + '}';
    }

    public boolean isWildcard() {
      return mapField == null;
    }
  }

  public static class ArraySpec {
    private final List<Integer> arraySpec;
    private final String name;

    public ArraySpec(List<Integer> arraySpec) {
      ArrayList<Integer> mine = new ArrayList<>(arraySpec);
      this.arraySpec = Collections.unmodifiableList(mine);
      this.name = stringVariant(arraySpec);
    }

    private String stringVariant(List<Integer> spec) {
      StringBuilder sb = new StringBuilder();
      int[] arr = spec.stream().mapToInt(i -> i.intValue()).toArray();
      int len = arr.length;
      int idx = 0, idx2 = 0;
      boolean needsComma = false;
      sb.append('[');
      while (idx < len) {
        while (++idx2 < len && arr[idx2] - arr[idx2 - 1] == 1) {
        }
        if (idx2 - idx > 2) {
          if (needsComma) {
            sb.append(", ");
          } else {
            needsComma = true;
          }
          sb.append(arr[idx]);
          sb.append('-');
          sb.append(arr[idx2 - 1]);
          idx = idx2;
        } else {
          for (; idx < idx2; idx++) {
            if (needsComma) {
              sb.append(", ");
            } else {
              needsComma = true;
            }
            sb.append(arr[idx]);
          }
        }
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
      return arraySpec.equals(spec.arraySpec);
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
      if (isWildcard()) {
        return "ArraySpec-wildcard";
      }
      return "ArraySpec{" + name + " == " + arraySpec + '}';
    }

    public boolean isWildcard() {
      return getArrayMembers().isEmpty();
    }

    public List<Integer> getArrayMembers() {
      return arraySpec;
    }
  }

  private final Type type;
  private final ArraySpec arraySpec;
  private final MapReference mapRef;

  public enum Type {
    MAP,
    ARRAY
  }

  public FieldReference(List<Integer> arraySpec) {
    this.arraySpec = new ArraySpec(arraySpec);
    this.type = Type.ARRAY;
    this.mapRef = null;
  }

  public FieldReference() {
    this.type = Type.MAP;
    this.mapRef = new MapReference();
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

  public MapReference mapRef() {
    if (type == Type.MAP) {
      return mapRef;
    }
    throw new ClassCastException();
  }

  @Override
  public String toString() {
    return (type == Type.ARRAY ? arraySpec : mapRef).toString();
  }
}
