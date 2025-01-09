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

import com.terracottatech.tcson.Son;
import com.terracottatech.tcson.SonBytes;
import com.terracottatech.tcson.SonMapValue;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.SonValue;
import com.terracottatech.tcson.UTCMillisDate;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static com.terracottatech.tcson.SonBytes.toBase64;
import static com.terracottatech.tcson.printers.SonPrinter.escapeStringAndDoubleQuote;
import static java.util.stream.StreamSupport.stream;

public class JSONPrettyPrinter implements SonPrinter {
  private final Son.SONPrinters mode;
  private boolean compact;
  private String indention = "  ";

  public JSONPrettyPrinter(Son.SONPrinters mode, boolean compact) {
    this.mode = mode;
    this.compact = compact;
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
  public JSONPrettyPrinter indention(int spaces) {
    char[] c = new char[spaces];
    Arrays.fill(c, ' ');
    this.indention = new String(c);
    return this;
  }

  private boolean isVisible(SonValue v) {
    if (!v.getType().isStrictlyJSON()) {
      switch (mode) {
        case JSON_STRICT:
          throw new IllegalArgumentException(v.getType().name() + " value seen!");
        case JSON_LOSSY:
          return false;
        default:
          break;
      }
    }
    return true;
  }

  private void listPrint(Iterable<? extends SonValue> l, PrintWriter pw, String indent) {
    pw.print("[");
    endln(pw);
    String newIndent = indent + indention;
    boolean[] first = new boolean[] { true };
    stream(l.spliterator(), false).filter(this::isVisible).forEach(ent -> printListMember(pw, newIndent, first, ent));
    endln(pw);
    pw.print(indent(indent) + "]");
  }

  private void mapPrint(Iterable<? extends SonMapValue> m, PrintWriter pw, String indent) {
    pw.print("{");
    endln(pw);
    String newIndent = indent + indention;
    boolean[] first = new boolean[] { true };
    stream(m.spliterator(), false).filter(this::isVisible).forEach(ent -> printMapMember(pw, newIndent, first, ent));
    endln(pw);
    pw.print(indent(indent) + "}");
  }

  @Override
  public void printList(Iterable<? extends SonValue> m, Writer w) {
    PrintWriter pw = new PrintWriter(w);
    listPrint(m, pw, "");
    pw.flush();
  }

  private void printListMember(PrintWriter pw, String newIndent, boolean[] first, SonValue ent) {
    if (first[0]) {
      first[0] = false;
    } else {
      pw.print(",");
      endln(pw);
    }
    Object obj = ent.getValue();
    SonType typ = ent.getType();
    switch (typ) {
      case NULL:
        pw.print(indent(newIndent) + "null");
        break;
      case MAP:
        pw.print(indent(newIndent));
        mapPrint(ent.mapValue(), pw, newIndent);
        break;
      case LIST:
        pw.print(indent(newIndent));
        listPrint(ent.listValue(), pw, newIndent);
        break;
      case CHAR:
      case STRING:
        pw.print(indent(newIndent) + escapeStringAndDoubleQuote(Objects.toString(obj)));
        break;
      case INT:
      case BYTE:
      case SHORT:
      case LONG:
      case FLOAT:
      case DOUBLE:
      case BOOL:
        pw.print(indent(newIndent) + obj);
        break;
      case BYTES:
        SonBytes sbs = (SonBytes) obj;
        String p = sbs.getSignifier() + "#" + toBase64(sbs.getBuffer());
        pw.print(indent(newIndent) + "\"" + p + '"');
        break;
      case DATE:
        pw.print(indent(newIndent) + "\"" + ((UTCMillisDate) obj).utcMillis() + "\"");
        break;
      case UUID:
        pw.print(indent(newIndent) + "\"" + ((UUID) obj).toString() + "\"");
        break;
      default:
        throw new IllegalStateException();
    }
  }

  @Override
  public void printMap(Iterable<? extends SonMapValue> m, Writer w) {
    PrintWriter pw = new PrintWriter(w);
    mapPrint(m, pw, "");
    pw.flush();
  }

  private void printMapMember(PrintWriter pw, String newIndent, boolean[] first, SonMapValue ent) {
    if (first[0]) {
      first[0] = false;
    } else {
      pw.print(",");
      endln(pw);
    }
    String key = escapeStringAndDoubleQuote(ent.getKey());
    Object obj = ent.getValue();
    SonType typ = ent.getType();
    switch (typ) {
      case MAP:
        pw.print(indent(newIndent) + key + sep());
        mapPrint(ent.mapValue(), pw, newIndent);
        break;
      case LIST:
        pw.print(indent(newIndent) + key + sep());
        listPrint(ent.listValue(), pw, newIndent);
        break;
      case CHAR:
      case STRING:
        pw.print(indent(newIndent) + key + sep() + escapeStringAndDoubleQuote(Objects.toString(obj)));
        break;
      case BYTE:
      case SHORT:
      case INT:
      case LONG:
      case FLOAT:
      case DOUBLE:
      case BOOL:
        pw.print(indent(newIndent) + key + sep() + obj.toString());
        break;
      case BYTES:
        SonBytes sbs = (SonBytes) obj;
        String p = sbs.getSignifier() + "#" + toBase64(sbs.getBuffer());
        pw.print(indent(newIndent) + key + sep() + '"' + p + '"');
        break;
      case DATE:
        pw.print(indent(newIndent) + key + sep() + '"' + ((UTCMillisDate) obj).utcMillis() + '"');
        break;
      case UUID:
        pw.print(indent(newIndent) + key + sep() + '"' + ((UUID) obj).toString() + '"');
        break;
      case NULL:
        pw.print(indent(newIndent) + key + sep() + "null");
        break;
      default:
        throw new IllegalStateException(typ + " seen");
    }
  }

  private String sep() {
    if (compact) {
      return ":";
    }
    return " : ";
  }

}

