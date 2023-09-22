/*
 * Copyright (c) 2020-2023 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.terracottatech.tcson.query;

import com.terracottatech.tcson.MutableSonList;
import com.terracottatech.tcson.MutableSonMap;
import com.terracottatech.tcson.Son;
import com.terracottatech.tcson.SonParser;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.SonValue;
import com.terracottatech.tcson.parser.query.ParseException;
import com.terracottatech.tcson.reading.ReadableSonValue;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static com.terracottatech.tcson.Son.writeableList;
import static com.terracottatech.tcson.Son.writeableMap;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SonDotTraversalTest {


  @Test
  public void testArrayThenMap2() throws ParseException {
    MutableSonList list = writeableList().builder().add("test12").add(writeableMap().builder()
                                                                                    .put("one", 1)
                                                                                    .put("two", "two")
                                                                                    .put("mappy", writeableList().builder()
                                                                                                                 .add("hey")
                                                                                                                 .add("bar")
                                                                                                                 .get())
                                                                                    .get()).get();

    SonDotTraversal t = Son.dotParser().parse("[0]");
    List<SonValue> got = t.matches(list, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).stringValue(), is("test12"));

    t = Son.dotParser().parse("[].one");
    got = t.matches(list, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).intValue(), is(1));

    t = Son.dotParser().parse("[].two");
    got = t.matches(list, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).stringValue(), is("two"));

    t = Son.dotParser().parse("[0:9].two");
    got = t.matches(list, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).stringValue(), is("two"));

    t = Son.dotParser().parse("[].mappy.[0,1]");
    got = t.matches(list, false);
    assertThat(got.size(), is(2));
    assertThat(got.get(0).stringValue(), is("hey"));
    assertThat(got.get(1).stringValue(), is("bar"));

  }

  @Test
  public void testMapThenArray2() throws ParseException {
    MutableSonMap map = writeableMap().builder().put("test12", 10l).put("il", writeableList().builder()
                                                                                             .add(1)
                                                                                             .add(2)
                                                                                             .add(writeableMap().builder()
                                                                                                                .put("hey", "bar")
                                                                                                                .get())
                                                                                             .get()).get();

    SonDotTraversal t = Son.dotParser().parse("il.[2].hey");
    List<SonValue> got = t.matches(map, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).stringValue(), is("bar"));

    t = Son.dotParser().parse("il.[1,2].hey");
    got = t.matches(map, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).stringValue(), is("bar"));

    t = Son.dotParser().parse("il.[]");
    got = t.matches(map, false);
    assertThat(got.size(), is(3));
    assertThat(got.get(0).intValue(), is(1));
    assertThat(got.get(1).intValue(), is(2));
    assertThat(got.get(2).getType(), is(SonType.MAP));

    t = Son.dotParser().parse("il.[]");
    got = t.matches(map, true);
    assertThat(got.size(), is(2));
    assertThat(got.get(0).intValue(), is(1));
    assertThat(got.get(1).intValue(), is(2));

  }

  @Test
  public void testNestedMap2() throws ParseException {
    MutableSonMap map = writeableMap().builder().put("test12", 10l).put("im", writeableMap().builder()
                                                                                            .put("one", 1)
                                                                                            .put("two", 2)
                                                                                            .get()).get();
    SonDotTraversal t = Son.dotParser().parse("im.two");
    List<SonValue> got = t.matches(map, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).intValue(), is(2));
  }

  @Test
  public void testNestedNestedMap2() throws ParseException {
    MutableSonMap map = writeableMap().builder().put("test12", 10l).put("im", writeableMap().builder()
                                                                                            .put("one", 1)
                                                                                            .put("two", 2)
                                                                                            .put("three", writeableMap()
                                                                                                            .builder()
                                                                                                            .put("hey", "bar")
                                                                                                            .get())
                                                                                            .get()).get();
    SonDotTraversal t = Son.dotParser().parse("im.three.hey");
    List<SonValue> got = t.matches(map, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).stringValue(), is("bar"));
  }

  @Test
  public void testPokedex2() throws IOException, com.terracottatech.tcson.parser.ParseException, ParseException {
    try (InputStream is = this.getClass().getResourceAsStream("/pokedex.json")) {
      SonParser parser = Son.parser().use(new InputStreamReader(is));
      MutableSonMap rootMap = parser.map();
      SonDotTraversal m = Son.dotParser().parse("pokemon.[7].weaknesses.[]");
      List<SonValue> got = m.matches(rootMap, false);
      Assert.assertThat(got, Matchers.hasItems(new ReadableSonValue(SonType.STRING, "Electric"), new ReadableSonValue(SonType.STRING, "Grass")));
      Matcher<Iterable<SonValue>> mm = Matchers.hasItems(Son.of("Electric"), Son.of("Grass"));

      Assert.assertTrue(mm.matches(got));
    }
  }

  @Test
  public void testSimpleArray2() throws ParseException {
    MutableSonList list = writeableList().builder().add(1).add("foo").get();
    SonDotTraversal t = Son.dotParser().parse("[0]");
    List<SonValue> got = t.matches(list, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).intValue(), is(1));

    t = Son.dotParser().parse("[]");
    got = t.matches(list, false);
    assertThat(got.size(), is(2));
    assertThat(got.get(0).intValue(), is(1));
    assertThat(got.get(1).stringValue(), is("foo"));
  }

  @Test
  public void testSimpleArrayDiscreteMultiples2() throws ParseException {
    MutableSonList list = writeableList().builder().add(1).add("foo").add(true).get();
    SonDotTraversal t = Son.dotParser().parse("[0,3]");
    List<SonValue> got = t.matches(list, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).intValue(), is(1));

    t = Son.dotParser().parse("[2,1]");
    got = t.matches(list, false);
    assertThat(got.size(), is(2));
    assertThat(got.get(0).boolValue(), is(true));
    assertThat(got.get(1).stringValue(), is("foo"));
  }

  @Test
  public void testSimpleArrayRanges2() throws ParseException {
    MutableSonList list = writeableList().builder().add(1).add("foo").add(true).get();
    SonDotTraversal t = Son.dotParser().parse("[1:1]");
    List<SonValue> got = t.matches(list, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).stringValue(), is("foo"));

    t = Son.dotParser().parse("[1:9,1]");
    got = t.matches(list, false);
    assertThat(got.size(), is(3));
    assertThat(got.get(0).stringValue(), is("foo"));
    assertThat(got.get(1).boolValue(), is(true));
    assertThat(got.get(2).stringValue(), is("foo"));

  }

  @Test
  public void testSimpleMap2() throws ParseException {
    MutableSonMap map = writeableMap().builder().put("test12", true).put("flu", "ffy").get();
    SonDotTraversal t = Son.dotParser().parse("test12");
    List<SonValue> got = t.matches(map, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).boolValue(), is(true));

    t = Son.dotParser().parse(".[]");
    got = t.matches(map, false);
    assertThat(got.size(), is(2));

    t = Son.dotParser().parse(".");
    got = t.matches(map, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).mapValue(), is(map));
  }

  @Test
  public void testNegativeArray() throws ParseException {
    MutableSonList list = writeableList().builder()
                            .add(1)
                            .add(2)
                            .add(3)
                            .add(4)
                            .add(5)
                            .get();
    SonDotTraversal t = Son.dotParser().parse("[-1]");
    List<SonValue> got = t.matches(list, false);
    assertThat(got.size(), is(1));
    assertThat(got.get(0).intValue(), is(5));

    t = Son.dotParser().parse("[2:-1]");
    got = t.matches(list, false);
    assertThat(got.size(), is(3));
    assertThat(got.get(0).intValue(), is(3));
    assertThat(got.get(1).intValue(), is(4));
    assertThat(got.get(2).intValue(), is(5));

  }
}
