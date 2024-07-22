/*
 * Copyright Super iPaaS Integration LLC, an IBM Company 2020, 2024
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ParserSupport {

  public static String unescapeQuoted(String s) throws IOException {
    StringBuilder sb = new StringBuilder(s.length());
    for (int i = 1; i < s.length() - 1; ++i) {
      char c = s.charAt(i);
      if (c == '\\') {
        if (i + 1 < s.length() - 1) {
          ++i;
          char c2 = s.charAt(i);
          switch (c2) {
            case 'u':
              int len = processUTF(s, i + 1, sb);
              i = i + len;
              break;
            case 'n':
              sb.append('\n');
              break;
            case 'r':
              sb.append('\r');
              break;
            case 'b':
              sb.append('\b');
              break;
            case 't':
              sb.append('\t');
              break;
            case 'f':
              sb.append('\f');
              break;
            default:
              sb.append(c2);
          }
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  public static String unescapeUnquoted(String s) throws IOException {
    StringBuilder sb = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c == '\\') {
        if (i + 1 < s.length() - 1) {
          ++i;
          char c2 = s.charAt(i);
          switch (c2) {
            case 'u':
              int len = processUTF(s, i + 1, sb);
              i = i + len;
              break;
            case 'n':
              sb.append('\n');
              break;
            case 'r':
              sb.append('\r');
              break;
            case 'b':
              sb.append('\b');
              break;
            case 't':
              sb.append('\t');
              break;
            case 'f':
              sb.append('\f');
              break;
            default:
              sb.append(c2);
          }
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  private static int processUTF(String src, int pos, StringBuilder dest) throws IOException {
    int value;
    try {
      value = Integer.parseInt(src.substring(pos, pos + 4), 16);
      dest.append(Character.toChars(value));
      return 4;
    } catch (NumberFormatException nfe) {
      IOException pe = new IOException("invalid hex value for \\u escape");
      throw pe;
    }
  }

  public static ByteBuffer parseB64(String s) {
    return ByteBuffer.wrap(Base64.getDecoder().decode(s));
  }

  public static List<Integer> parseArraySpec(String spec) throws com.terracottatech.tcson.parser.query.ParseException {
    ArrayList<Integer> refs = new ArrayList<>();
    // trim the ending square brackets
    String s = spec.trim();
    s = s.substring(1, s.length() - 1);
    if (s.length() > 0) {
      String[] parts = s.split(",");
      for (String p : parts) {
        String[] ends = p.trim().split("-");
        if (ends.length == 1) {
          refs.add(Integer.parseInt(ends[0]));
        } else {
          int start = Integer.parseInt(ends[0]);
          int end = Integer.parseInt(ends[1]);
          if (start > end) {
            throw new com.terracottatech.tcson.parser.query.ParseException("Array specs much be in order: " + spec);
          }
          for (int i = start; i <= end; i++) {
            refs.add(i);
          }
        }
      }
    }
    return refs;
  }
}


