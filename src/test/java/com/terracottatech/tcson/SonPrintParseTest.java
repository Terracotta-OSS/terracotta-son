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
package com.terracottatech.tcson;

import com.terracottatech.tcson.mutable.MutableSonMapBuilder;
import com.terracottatech.tcson.parser.ParseException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class SonPrintParseTest {

  @Test
  public void testAll() throws IOException {
    doResource("/simplemap.son");
    doResource("/simplelist.son");
    doResource("/mapandlist.son");
  }

  private void doResource(String res) throws IOException {
    try (InputStream is = this.getClass().getResourceAsStream(res)) {
      doStream(new InputStreamReader(is));
    }
  }

  public void doStream(Reader r) {
    Son.parser().use(r).stream().forEach((sv) -> {
      switch (sv.getType()) {
        case MAP:
          doMap(sv.mapValue());
          break;
        case LIST:
          doList(sv.listValue());
          break;
        default:
          break;
      }
    });
  }

  private void doMap(MutableSonMap value) {
    try {
      String s = Son.SONPrinters.SON.compact().printMap(value);
      MutableSonMap m2 = Son.parser().use(s).map();
      Assert.assertTrue(value.deepEquals(m2));

      s = Son.SONPrinters.SON.pretty().printMap(value);
      m2 = Son.parser().use(s).map();
      Assert.assertTrue(value.deepEquals(m2));

      s = Son.SONPrinters.SON_VERBOSE.compact().printMap(value);
      m2 = Son.parser().use(s).map();
      Assert.assertTrue(value.deepEquals(m2));

      s = Son.SONPrinters.SON_VERBOSE.pretty().printMap(value);
      m2 = Son.parser().use(s).map();
      Assert.assertTrue(value.deepEquals(m2));

    } catch (ParseException e) {
      Assert.fail(e.getMessage());
    }
  }

  private void doList(MutableSonList value) {
    try {
      String s = Son.SONPrinters.SON.compact().printList(value);
      MutableSonList m2 = Son.parser().use(s).list();
      Assert.assertTrue(value.deepEquals(m2));

      s = Son.SONPrinters.SON.pretty().printList(value);
      m2 = Son.parser().use(s).list();
      Assert.assertTrue(value.deepEquals(m2));

      s = Son.SONPrinters.SON_VERBOSE.compact().printList(value);
      m2 = Son.parser().use(s).list();
      Assert.assertTrue(value.deepEquals(m2));

      s = Son.SONPrinters.SON_VERBOSE.pretty().printList(value);
      m2 = Son.parser().use(s).list();
      Assert.assertTrue(value.deepEquals(m2));
    } catch (ParseException e) {
      Assert.fail(e.getMessage());
    }
  }

  @Test
  @Ignore
  public void testGenerate() {
    MutableSonMapBuilder writer = Son.writeableMap().builder();
    writer.put("int", 10);
    writer.put("foo", "happy");
    writer.put("blah", 'l');
    writer.put("fl", 32.5f);
    writer.put("lst", Son.writeableList()
                         .builder()
                         .add(10)
                         .add(11)
                         .add("hi!")
                         .addNull()
                         .add(Son.writeableMap()
                                 .builder()
                                 .put("chris", true)
                                 .putNull("f")
                                 .put("bob", "na ra")
                                 .get())
                         .add(32.5f)
                         .get());
    writer.put("barr", new SonBytes((byte) 1, new byte[] { 1, 2, 4, 8 }, 0, 4));
    writer.put("barr-none", new SonBytes((byte) 1, new byte[0], 0, 0));
    MutableSonMap m = writer.get();

    System.out.println(Son.SONPrinters.SON.compact().printList(m));
    System.out.println(Son.SONPrinters.SON.pretty().printList(m));
    System.out.println(Son.SONPrinters.SON_VERBOSE.compact().printList(m));
    System.out.println(Son.SONPrinters.SON_VERBOSE.pretty().printList(m));
  }
}
