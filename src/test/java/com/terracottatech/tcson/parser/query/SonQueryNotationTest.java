/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.parser.query;

import com.terracottatech.tcson.parser.FieldReference;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.hamcrest.Matchers.is;

public class SonQueryNotationTest {
  @Test
  public void testArraySpec() throws ParseException {
    SonQueryParser p = new SonQueryParser(new StringReader("[]"));
    List<Integer> l = p.arraySpec().arrSpec().getArrayMembers();
    Assert.assertThat(l.size(), is(0));

    p = new SonQueryParser(new StringReader("[10]"));
    l = p.arraySpec().arrSpec().getArrayMembers();
    Assert.assertThat(l.size(), is(1));
    Assert.assertThat(l.get(0), is(10));

    p = new SonQueryParser(new StringReader("[10,12]"));
    l = p.arraySpec().arrSpec().getArrayMembers();
    Assert.assertThat(l.size(), is(2));
    Assert.assertThat(l.get(0), is(10));
    Assert.assertThat(l.get(1), is(12));

    p = new SonQueryParser(new StringReader("[ 10, 12 ]"));
    l = p.arraySpec().arrSpec().getArrayMembers();
    Assert.assertThat(l.size(), is(2));
    Assert.assertThat(l.get(0), is(10));
    Assert.assertThat(l.get(1), is(12));

    p = new SonQueryParser(new StringReader("[ 1-1 ]"));
    l = p.arraySpec().arrSpec().getArrayMembers();
    Assert.assertThat(l.size(), is(1));
    Assert.assertThat(l.get(0), is(1));

    p = new SonQueryParser(new StringReader("[ 1-10 ]"));
    l = p.arraySpec().arrSpec().getArrayMembers();
    Assert.assertThat(l.size(), is(10));
    Assert.assertThat(l.get(6), is(7));

    p = new SonQueryParser(new StringReader("[ 13, 1-10 ]"));
    l = p.arraySpec().arrSpec().getArrayMembers();
    Assert.assertThat(l.size(), is(11));
    Assert.assertThat(l.get(0), is(13));
    Assert.assertThat(l.get(10), is(10));
  }

  @Test
  public void testNotation() throws ParseException {
    SonQueryParser p = new SonQueryParser(new StringReader("foo.bar.baz"));
    List<FieldReference> dot = p.dotterSpec();
    Assert.assertThat(dot.size(), is(3));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("foo"));
    Assert.assertThat(dot.get(1).mapRef().getFieldName(), is("bar"));
    Assert.assertThat(dot.get(2).mapRef().getFieldName(), is("baz"));

    p = new SonQueryParser(new StringReader("foo.[0-1]"));
    dot = p.dotterSpec();
    System.out.println(dot);
    Assert.assertThat(dot.size(), is(2));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("foo"));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().size(), is(2));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(0), is(0));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().get(1), is(1));

    p = new SonQueryParser(new StringReader("."));
    dot = p.dotterSpec();
    Assert.assertThat(dot.size(), is(1));
    Assert.assertThat(dot.get(0).mapRef().isWildcard(), is(true));

    p = new SonQueryParser(new StringReader("foo.[1-10].bar.baz.[].tail"));
    dot = p.dotterSpec();
    Assert.assertThat(dot.size(), is(6));
    Assert.assertThat(dot.get(0).mapRef().getFieldName(), is("foo"));
    Assert.assertThat(dot.get(1).arrSpec().getArrayMembers().size(), is(10));
    Assert.assertThat(dot.get(2).mapRef().getFieldName(), is("bar"));
    Assert.assertThat(dot.get(3).mapRef().getFieldName(), is("baz"));
    Assert.assertThat(dot.get(4).arrSpec().isWildcard(), is(true));
    Assert.assertThat(dot.get(5).mapRef().getFieldName(), is("tail"));

  }

}
