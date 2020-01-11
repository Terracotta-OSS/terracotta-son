/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.parser;

import java.nio.ByteBuffer;
import java.util.Base64;

public class ParserSupport {

  public static String unescapeQuoted(String s) throws ParseException {
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
            case '\n':
              sb.append('\n');
              break;
            case '\r':
              sb.append('\r');
              break;
            case '\\':
              sb.append('\\');
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
            case '\'':
              sb.append('\'');
              break;
            case '/':
              sb.append('/');
              break;
            case '\"':
              sb.append('\"');
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

  private static int processUTF(String src, int pos, StringBuilder dest) throws ParseException {
    int value;
    try {
      value = Integer.parseInt(src.substring(pos, pos + 4), 16);
      dest.append(Character.toChars(value));
      return 4;
    } catch (NumberFormatException nfe) {
      ParseException pe = new ParseException("invalid hex value for \\u escape");
      pe.initCause(nfe);
      throw pe;
    }
  }

  public static ByteBuffer parseB64(String s) {
    return ByteBuffer.wrap(Base64.getDecoder().decode(s));
  }

}

