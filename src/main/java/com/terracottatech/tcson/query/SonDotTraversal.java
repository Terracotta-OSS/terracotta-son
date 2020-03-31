/*
 * Copyright (c) 2011-2018 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.query;

import com.terracottatech.tcson.SonList;
import com.terracottatech.tcson.SonMap;
import com.terracottatech.tcson.SonMapValue;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.SonValue;
import com.terracottatech.tcson.parser.FieldReference;
import com.terracottatech.tcson.parser.query.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SonDotTraversal {
  private final List<FieldReference> spec;

  public SonDotTraversal(String testing) throws ParseException {
    this(new SonDotParser().parse(testing).spec);
  }

  public SonDotTraversal(List<FieldReference> spec) {
    this.spec = spec;
  }

  public List<FieldReference> getTraversalSpec() {
    return spec;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SonDotTraversal traversal = (SonDotTraversal) o;
    return spec.equals(traversal.spec);
  }

  @Override
  public int hashCode() {
    return Objects.hash(spec);
  }

  public List<SonValue> matches(SonMap<?> start, boolean terminalsOnly) {
    return matches(start.asSonValue(), terminalsOnly);
  }

  public List<SonValue> matches(SonValue start, boolean terminalsOnly) {
    ArrayList<SonValue> current = new ArrayList<>();
    ArrayList<SonValue> next = new ArrayList<>();
    current.add(start);
    for (FieldReference fr : spec) {
      //System.out.println("Checking: " + fr + " vs " + current);
      for (SonValue mv : current) {
        switch (fr.getType()) {
          case WILD:
            if (mv.getType() == SonType.MAP) {
              SonMap<?> map = mv.mapValue();
              for (SonMapValue v : map) {
                SonMapValue p = map.get(v.getKey());
                next.add(p);
              }
            } else if (mv.getType() == SonType.LIST) {
              SonList<?> l = mv.listValue();
              for (SonValue sv : l) {
                next.add(sv);
              }
            }
            break;
          case MAP:
            if (mv.getType() == SonType.MAP) {
              SonMap<?> map = mv.mapValue();
              mv = map.get(fr.mapRef().getFieldName());
              if (mv != null) {
                next.add(mv);
              }
            }
            break;
          case ARRAY:
            if (mv.getType() == SonType.LIST) {
              FieldReference.ArraySpec sp = fr.arrSpec();
              SonList<?> l = mv.listValue();
              for (FieldReference.ArraySlice slice : sp.getArrayMembers()) {
                next.addAll(slice.extract(l));
              }
            }
            break;
          default:
            break;
        }
      }
      ArrayList<SonValue> tmp = current;
      current = next;
      next = tmp;
      next.clear();
    }
    List<SonValue> ret = current.stream()
                           .filter(sv -> !terminalsOnly ||
                                         (sv.getType() != SonType.LIST &&
                                          sv.getType() != SonType.MAP))
                           .collect(Collectors.toList());
    return ret;
  }

  public List<SonValue> matches(SonList<?> start, boolean terminalsOnly) {
    return matches(start.asSonValue(), terminalsOnly);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (FieldReference i : spec) {
      switch (i.getType()) {
        case WILD:
          sb.append(".[]");
          break;
        case MAP:
          FieldReference.MapReference m = i.mapRef();
          sb.append('.');
          sb.append(m.getFieldName());
          break;
        case ARRAY:
          FieldReference.ArraySpec a = i.arrSpec();
          sb.append('.');
          sb.append(a.getName());
          break;
        default:
          throw new IllegalStateException();
      }
    }
    return sb.toString();
  }
}