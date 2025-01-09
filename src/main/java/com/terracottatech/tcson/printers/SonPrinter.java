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
