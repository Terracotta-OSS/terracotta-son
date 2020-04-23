/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.printers;

import com.terracottatech.tcson.SonBytes;
import com.terracottatech.tcson.SonMapValue;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.SonValue;
import com.terracottatech.tcson.UTCMillisDate;

import java.io.PrintWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static com.terracottatech.tcson.SonBytes.toBase64;
import static com.terracottatech.tcson.printers.SonPrinter.escapeStringAndDoubleQuote;

/**
 * Rough spec is<pre>{@code
 *
 *    list :== [ <value>, ... ]
 *    value :== <map> | <list> | <type> : <value>
 *    map entry :== "key" : <value>
 *    map :== { <map entry>, ... }
 *
 *    type :== i8 | i16 | i32 | i64 | f32 | f64 | bool | chr | str | bin | map | list
 *
 *    value : <int value> | <float value | <char value> | <bool value> | <string value> |
 *       <bin value> | <map> | <list>
 *
 * }</pre>
 */
public class SonPrettyPrinter implements SonPrinter {
  private final boolean compact;
  private final boolean verbose;
  private String indention = "  ";

  public SonPrettyPrinter(boolean oneLine, boolean verbose) {
    this.compact = oneLine;
    this.verbose = verbose;
  }

  static String typeString(SonType t) {
    switch (t) {
      case STRING:
        return "string";
      case MAP:
        return "map";
      case LIST:
        return "list";
      case INT:
        return "int";
      case CHAR:
        return "char";
      case LONG:
        return "long";
      case FLOAT:
        return "float";
      case BYTES:
        return "bytes";
      case BYTE:
        return "byte";
      case SHORT:
        return "short";
      case DOUBLE:
        return "double";
      case BOOL:
        return "bool";
      case DATE:
        return "utc";
      case UUID:
        return "uuid";
      case NULL:
        return "null";
      default:
        throw new IllegalStateException(t + "");
    }
  }

  private static String floatString(Float f) {
    if (f.isInfinite() || f.isNaN()) {
      return f.toString();
    }
    return f.toString() + "f";
  }

  private static String doubleString(Double f) {
    if (f.isInfinite() || f.isNaN()) {
      return f.toString();
    }
    return f.toString() + "d";
  }

  static String toHexString(ByteBuffer obj) {
    StringBuilder sb = new StringBuilder();
    for (int i = obj.position(); i < obj.limit(); i++) {
      int p = obj.get(i) & 0xff;
      sb.append(String.format("%02x", p));
    }
    return sb.toString();
  }

  private void endln(PrintWriter pw) {
    if (!compact) {
      pw.println();
    }
  }

  private String indent(String indent) {
    if (compact) {
      return "";
    }
    return indent;
  }

  @Override
  public SonPrettyPrinter indention(int spaces) {
    char[] c = new char[spaces];
    Arrays.fill(c, ' ');
    this.indention = new String(c);
    return this;
  }

  private void listPrint(Iterable<? extends SonValue> l, PrintWriter pw, String indent) {
    pw.print("[");
    endln(pw);
    String newIndent = indent + indention;
    boolean first = true;
    for (SonValue ent : l) {
      if (first) {
        first = false;
      } else {
        pw.print(",");
        endln(pw);
      }

      SonType typ = ent.getType();
      Object obj = ent.getValue();
      pw.print(indent(newIndent));
      if (verbose || needsType(typ)) {
        pw.print(typeString(typ) + sep());
      }
      switch (typ) {
        case MAP:
          mapPrint(ent.mapValue(), pw, newIndent);
          break;
        case LIST:
          listPrint(ent.listValue(), pw, newIndent);
          break;
        default:
          String str = valueString(typ, obj);
          pw.print(str);
          break;
      }
    }
    endln(pw);
    pw.print(indent(indent) + "]");
  }

  private void mapPrint(Iterable<? extends SonMapValue> m, PrintWriter pw, String indent) {
    pw.print("{");
    endln(pw);
    boolean first = true;
    String newIndent = indent + indention;
    for (SonMapValue ent : m) {
      if (first) {
        first = false;
      } else {
        pw.print(",");
        endln(pw);
      }
      String key = escapeStringAndDoubleQuote(ent.getKey());
      Object obj = ent.getValue();
      SonType typ = ent.getType();
      pw.print(indent(newIndent) + key + sep());
      if (verbose || needsType(typ)) {
        pw.print(typeString(typ) + sep());
      }
      switch (typ) {
        case MAP:
          mapPrint(ent.mapValue(), pw, newIndent);
          break;
        case LIST:
          listPrint(ent.listValue(), pw, newIndent);
          break;
        default:
          String str = valueString(typ, obj);
          pw.print(str);
          break;
      }
    }
    endln(pw);
    pw.print(indent(indent) + "}");
  }

  private boolean needsType(SonType typ) {
    switch (typ) {
      case STRING:
      case LONG:
      case DOUBLE:
      case LIST:
      case MAP:
      case CHAR:
      case BOOL:
      case NULL:
        return false;
      case FLOAT:
      case INT:
      case BYTES:
      case BYTE:
      case SHORT:
      case DATE:
      case UUID:
        return true;
      default:
        throw new IllegalArgumentException();
    }
  }

  @Override
  public void printList(Iterable<? extends SonValue> m, Writer w) {
    PrintWriter pw = new PrintWriter(w);
    listPrint(m, pw, "");
    pw.flush();
  }

  @Override
  public void printMap(Iterable<? extends SonMapValue> m, Writer w) {
    PrintWriter pw = new PrintWriter(w);
    mapPrint(m, pw, "");
    pw.flush();
  }

  private String sep() {
    if (compact) {
      return ":";
    }
    return " : ";
  }

  String valueString(SonType typ, Object obj) {
    switch (typ) {
      case BOOL:
        return ((Boolean) obj) ? "true" : "false";
      case BYTES:
        SonBytes sbs = (SonBytes) obj;
        if (sbs.getSignifier() != (byte) 0) {
          return sbs.getSignifier() + "#" + toBase64(sbs.getBuffer());
        } else {
          return toBase64(sbs.getBuffer());
        }
      case CHAR: {
        char ch = obj.toString().charAt(0);
        if (ch == '\'' || ch == '\\') {
          return "'\\" + ch + "'";
        } else {
          return "'" + ch + "'";
        }
      }
      case FLOAT:
        return floatString((Float) obj);
      case DOUBLE:
        return doubleString((Double) obj);
      case LONG:
        return obj.toString();
      case STRING:
        StringBuilder sb = new StringBuilder();
        escapeStringAndDoubleQuote((String) obj, sb);
        return sb.toString();
      case DATE:
        return Long.toString(((UTCMillisDate) obj).utcMillis());
      case UUID:
        UUID uuid = ((UUID) obj);
        return uuid.toString();
      case NULL:
        return "null";
      default:
        return Objects.toString(obj);
    }
  }

}
