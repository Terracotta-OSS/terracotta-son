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
package com.terracottatech.tcson.parser.query;

import com.terracottatech.tcson.parser.FieldReference;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SonQueryNotationTest {
  @Test
  public void testArraySpec() throws ParseException {
    SonQueryParser p = new SonQueryParser(new StringReader("[]"));
    assertThat(p.angleStep().isWild(), is(true));

    p = new SonQueryParser(new StringReader("[10]"));
    List<FieldReference.ArraySlice> l = p.angleStep().arrSpec().getArrayMembers();
    assertThat(l.size(), is(1));
    assertThat(l.get(0).getLeft(), is(10));
    assertThat(l.get(0).getRight(), is(10));

    p = new SonQueryParser(new StringReader("[10:12]"));
    l = p.angleStep().arrSpec().getArrayMembers();
    assertThat(l.size(), is(1));
    assertThat(l.get(0).getLeft(), is(10));
    assertThat(l.get(0).getRight(), is(12));

    p = new SonQueryParser(new StringReader("[ 10, 12 ]"));
    l = p.angleStep().arrSpec().getArrayMembers();
    assertThat(l.size(), is(2));
    assertThat(l.get(0).getLeft(), is(10));
    assertThat(l.get(0).getRight(), is(10));
    assertThat(l.get(1).getLeft(), is(12));
    assertThat(l.get(1).getRight(), is(12));

    p = new SonQueryParser(new StringReader("[ 1:-1 ]"));
    l = p.angleStep().arrSpec().getArrayMembers();
    assertThat(l.size(), is(1));
    assertThat(l.get(0).getLeft(), is(1));
    assertThat(l.get(0).getRight(), is(-1));

    p = new SonQueryParser(new StringReader("[ 1:10 ]"));
    l = p.angleStep().arrSpec().getArrayMembers();
    assertThat(l.size(), is(1));
    assertThat(l.get(0).getLeft(), is(1));
    assertThat(l.get(0).getRight(), is(10));

    p = new SonQueryParser(new StringReader("[ 13, 1:10 ]"));
    l = p.angleStep().arrSpec().getArrayMembers();
    assertThat(l.size(), is(2));
    assertThat(l.get(0).getLeft(), is(13));
    assertThat(l.get(0).getRight(), is(13));
    assertThat(l.get(1).getLeft(), is(1));
    assertThat(l.get(1).getRight(), is(10));
  }

  @Test
  public void testSimple() throws ParseException {
    SonQueryParser p = new SonQueryParser(new StringReader("foo"));
    FieldReference dot = p.simpleStep();
    assertThat(dot.mapRef().getFieldName(), is("foo"));
  }

  @Test
  public void testQuoteRef() throws ParseException {
    SonQueryParser p = new SonQueryParser(new StringReader("\"foo\""));
    FieldReference dot = p.stringStep();
    assertThat(dot.mapRef().getFieldName(), is("foo"));
  }

  @Test
  public void testAngledString() throws ParseException {
    SonQueryParser p = new SonQueryParser(new StringReader("[\"foo\"]"));
    FieldReference dot = p.angleStep();
    assertThat(dot.mapRef().getFieldName(), is("foo"));
  }

  @Test
  public void testNotation() throws ParseException {
    SonQueryParser p = new SonQueryParser(new StringReader(".foo.bar.baz"));
    List<FieldReference> dot = p.dotSpec();
    Assert.assertThat(dot.size(), is(3));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("foo"));
    Assert.assertThat(dot.get(1).mapRef().getFieldName(), is("bar"));
    Assert.assertThat(dot.get(2).mapRef().getFieldName(), is("baz"));

    p = new SonQueryParser(new StringReader(".foo.[0:1]"));
    dot = p.dotSpec();
    Assert.assertThat(dot.size(), is(2));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("foo"));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().size(), is(1));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0).getLeft(), is(0));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0).getRight(), is(1));

    p = new SonQueryParser(new StringReader(".foo.[0:1].[4]"));
    dot = p.dotSpec();
    Assert.assertThat(dot.size(), is(3));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("foo"));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().size(), is(1));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0).getLeft(), is(0));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0).getRight(), is(1));
    Assert.assertThat(dot.get(2).arrSpec().getArrayMembers().get(0).getRight(), is(4));
    Assert.assertThat(dot.get(2).arrSpec().getArrayMembers().get(0).getLeft(), is(4));

    p = new SonQueryParser(new StringReader(".next_evolution.[].name"));
    dot = p.dotSpec();
    Assert.assertThat(dot.size(), is(3));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("next_evolution"));
    Assert.assertThat(dot.get(1).isWild(), is(true));
    Assert.assertThat(dot.get(2).mapRef().getFieldName(), is("name"));

    p = new SonQueryParser(new StringReader("next_evolution.[].name"));
    dot = p.dotSpec();
    Assert.assertThat(dot.size(), is(3));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("next_evolution"));
    Assert.assertThat(dot.get(1).isWild(), is(true));
    Assert.assertThat(dot.get(2).mapRef().getFieldName(), is("name"));

    p = new SonQueryParser(new StringReader("foo.[0:1]"));
    dot = p.dotSpec();
    Assert.assertThat(dot.size(), is(2));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("foo"));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().size(), is(1));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0).getLeft(), is(0));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0).getRight(), is(1));

    p = new SonQueryParser(new StringReader(".foo.[0:-1].[4]"));
    dot = p.dotSpec();
    Assert.assertThat(dot.size(), is(3));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("foo"));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0).getLeft(), is(0));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0).getRight(), is(-1));
    Assert.assertThat(dot.get(2).arrSpec().getArrayMembers().get(0).getLeft(), is(4));
    Assert.assertThat(dot.get(2).arrSpec().getArrayMembers().get(0).getRight(), is(4));

    p = new SonQueryParser(new StringReader(".[]"));
    dot = p.dotSpec();
    Assert.assertThat(dot.size(), is(1));
    Assert.assertThat(dot.get(0).isWild(), is(true));

    try {
      p = new SonQueryParser(new StringReader("some list.[]"));
      p.dotSpec();
      Assert.fail();
    } catch(ParseException e) {
    }

    p = new SonQueryParser(new StringReader("[some list].[]"));
    dot = p.dotSpec();
    assertThat(dot.size(), is(2));
    assertThat(dot.get(0).mapRef().getFieldName(), is("some list"));
    assertThat(dot.get(1).isWild(), is(true));

    p = new SonQueryParser(new StringReader(".[some list].[]"));
    dot = p.dotSpec();
    assertThat(dot.size(), is(2));
    assertThat(dot.get(0).mapRef().getFieldName(), is("some list"));
    assertThat(dot.get(1).isWild(), is(true));

    p = new SonQueryParser(new StringReader("foo.[ 1:10 ].bar.baz.[].tail"));
    dot = p.dotSpec();
    Assert.assertThat(dot.size(), is(6));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("foo"));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0).getLeft(), is(1));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0).getRight(), is(10));
    Assert.assertThat(dot.get(2).mapRef().getFieldName(), is("bar"));
    Assert.assertThat(dot.get(3).mapRef().getFieldName(), is("baz"));
    Assert.assertThat(dot.get(4).isWild(), is(true));
    Assert.assertThat(dot.get(5).mapRef().getFieldName(), is("tail"));

    p = new SonQueryParser(new StringReader("."));
    dot = p.dotSpec();
    assertThat(dot.size(), is(0));

  }

}
