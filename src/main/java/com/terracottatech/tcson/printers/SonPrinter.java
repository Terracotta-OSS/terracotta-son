/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.printers;

import com.terracottatech.tcson.SonMapValue;
import com.terracottatech.tcson.SonValue;

import java.io.StringWriter;
import java.io.Writer;

public interface SonPrinter {

  static String escapeStringAndDoubleQuote(String s) {
    StringBuilder sb = new StringBuilder();
    escapeStringAndDoubleQuote(s, sb);
    return sb.toString();
  }

  static void escapeStringAndDoubleQuote(String s, StringBuilder sb) {
    final int len = s.length();
    sb.append('"');
    for (int i = 0; i < len; i++) {
      char ch = s.charAt(i);
      switch (ch) {
        case '"':
          sb.append("\\\"");
          break;
        case '\\':
          sb.append("\\\\");
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\r':
          sb.append("\\r");
          break;
        case '\t':
          sb.append("\\t");
          break;
        default:
          if ((ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
            String ss = Integer.toHexString(ch);
            sb.append("\\u");
            for (int k = 0; k < 4 - ss.length(); k++) {
              sb.append('0');
            }
            sb.append(ss.toUpperCase());
          } else {
            sb.append(ch);
          }
      }
    }
    sb.append('"');
  }

  SonPrinter indention(int spaces);

  default String printList(Iterable<? extends SonValue> m) {
    StringWriter sw = new StringWriter();
    printList(m, sw);
    return sw.toString();
  }

  void printList(Iterable<? extends SonValue> m, Writer w);

  default String printMap(Iterable<? extends SonMapValue> m) {
    StringWriter sw = new StringWriter();
    printMap(m, sw);
    return sw.toString();
  }

  void printMap(Iterable<? extends SonMapValue> m, Writer w);
}
