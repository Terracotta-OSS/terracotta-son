/*
 * Copyright (c) 2011-2023 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.parser;

import com.terracottatech.tcson.MutableSonList;
import com.terracottatech.tcson.MutableSonMap;
import com.terracottatech.tcson.Son;
import com.terracottatech.tcson.SonList;
import com.terracottatech.tcson.SonMapValue;
import com.terracottatech.tcson.SonParser;
import com.terracottatech.tcson.SonType;
import com.terracottatech.tcson.UTCMillisDate;
import com.terracottatech.tcson.mutable.MutableSonValue;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SonParserTest {

  @Rule
  public final TemporaryFolder temporaryFolder = new TemporaryFolder();

  private final long seed = new Random().nextLong();
  private final Random random = new Random(seed);

  @Test
  public void testNanInfinity() throws ParseException {
    StringWriter sw = new StringWriter();
    MutableSonMap m = Son.writeableMap();
    m.put("fnan", Float.NaN);
    m.put("dnan", Double.NaN);
    m.put("finfplus", Float.POSITIVE_INFINITY);
    m.put("dinfplus", Double.POSITIVE_INFINITY);
    m.put("finfneg", Float.NEGATIVE_INFINITY);
    m.put("dinfneg", Double.NEGATIVE_INFINITY);
    Son.SONPrinters.SON.pretty().printMap(m, sw);

    String s = sw.toString();
    //System.out.println(s);
    SonParser parser = new SonParser().use(s);
    MutableSonMap m2 = parser.map();
    //System.out.println(Son.SONPrinters.SON.pretty().print(m2));
    Assert.assertTrue(m2.deepEquals(m));
  }

  @Test
  public void testParsePokedex() throws IOException, ParseException {
    final List<MutableSonMap> maps = new ArrayList<>();
    try (InputStream is = this.getClass().getResourceAsStream("/pokedex.json")) {
      SonParser parser = Son.parser().use(new InputStreamReader(is));
      MutableSonMap rootMap = parser.map();
      rootMap.get("pokemon").listValue().forEach(msv -> maps.add(msv.mapValue()));
    }
    Assert.assertThat(maps.size(), is(151));

    StringWriter sw = new StringWriter();
    for (MutableSonMap m : maps) {
      Son.SONPrinters.SON.pretty().printMap(m, sw);
    }
    List<MutableSonMap>
      maps2 =
      Son.parser()
         .use(new StringReader(sw.toString()))
         .stream()
         .map(MutableSonValue::mapValue)
         .collect(Collectors.toList());

    Assert.assertThat(maps, is(maps2));
  }

  @Test
  public void testRoundTripChars() throws ParseException {
    MutableSonList l = Son.writeableList();
    l.add('d');
    l.add('\\');
    l.add('\r');
    l.add('\n');
    l.add('\'');
    String p = Son.SONPrinters.SON.printer(false).printList(l);
    System.out.println(p);

    MutableSonList l2 = Son.parser().use(p).list();
    assertThat(l.deepEquals(l2), is(true));
  }

  @Test
  public void testRoundTripStrings() throws ParseException {
    MutableSonList l = Son.writeableList();
    l.add("ddd");
    l.add("f\\f");
    l.add("f\"f");
    l.add("f\nf");
    l.add("f\rf");
    String p = Son.SONPrinters.SON.printer(false).printList(l);
    System.out.println(p);

    MutableSonList l2 = Son.parser().use(p).list();
    assertThat(l.deepEquals(l2), is(true));
  }

  @Test
  public void testParsePrintRoundTripOneMap() throws ParseException {
    MutableSonMap m = Son.writeableMap();
    m.put("int val", 10);
    m.put("double val", 32.2);
    m.put("string val", "third");
    m.put("bool val", true);
    m.put("utc date val", new UTCMillisDate());
    MutableSonMap subm = Son.writeableMap();
    subm.put("bytes val", (byte) 8, new byte[] { 9, 7, 5, 3, 1 });
    subm.put("bytes val2", (byte) 0, new byte[] { 11, 9, 7, 5, 3, 1 });
    subm.put("bytes val3", (byte) 0, new byte[0]);
    subm.put("long val", 10l);
    subm.put("uuid val", UUID.randomUUID());
    subm.put("char val", 'v');
    //subm.put("char nl", '\n');
    //subm.put("char cr", '\r');
    //subm.put("char tab", '\n');
    subm.put("char sq", '\'');
    subm.put("char bs", '\\');
    subm.putNull("null val");
    m.put("map value", subm);
    MutableSonList l = SonList.writeable();
    l.add(10).add(20L).add("hello").add('t');
    MutableSonMap ssm = Son.writeableMap();
    ssm.put("string again", "there");
    ssm.put("float val", 32.4f);
    l.add(ssm);
    m.put("list val", l);

    StringWriter sw = new StringWriter();
    Son.SONPrinters.SON.pretty().printMap(m, sw);
    String son = sw.toString();

    sw = new StringWriter();
    Son.SONPrinters.SON_VERBOSE.pretty().printMap(m, sw);
    String sonVerbose = sw.toString();

    //    System.out.println("SON: " + son);
    //    System.out.println("SON Verbose: " + sonVerbose);
    //    System.out.println("JSON Lossy: " + Son.SONPrinters.JSON_LOSSY.pretty().printMap(m));
    //    System.out.println("JSON Extended: " + Son.SONPrinters.JSON_EXTENDED.pretty().printMap(m));

    SonParser parser = new SonParser().use(son);
    MutableSonMap m2 = parser.map();
    Assert.assertTrue(m2.deepEquals(m));

    parser = new SonParser().use(sonVerbose);
    m2 = parser.map();
    Assert.assertTrue(m2.deepEquals(m));
  }

  @Test
  public void testSimpleList() throws ParseException {
    StringReader sr = new StringReader("[ int : 10 ]");
    SonParserImpl parser = new SonParserImpl(sr);
    MutableSonValue val = parser.sonList();
    Assert.assertThat(val.getType(), is(SonType.LIST));
    Assert.assertThat(val.listValue().size(), is(1));
    Assert.assertThat(val.listValue().get(0).intValue(), is(10));

    sr = new StringReader("[ int : 10, string: \"he\\rl\\\"lo\" ]");
    parser = new SonParserImpl(sr);
    val = parser.sonList();
    Assert.assertThat(val.getType(), is(SonType.LIST));
    Assert.assertThat(val.listValue().size(), is(2));
    Assert.assertThat(val.listValue().get(0).intValue(), is(10));
    Assert.assertThat(val.listValue().get(1).stringValue(), is("he\rl\"lo"));

    sr = new StringReader("list: [ int : 10 ]");
    parser = new SonParserImpl(sr);
    val = parser.sonList();
    Assert.assertThat(val.getType(), is(SonType.LIST));
    Assert.assertThat(val.listValue().size(), is(1));
    Assert.assertThat(val.listValue().get(0).intValue(), is(10));

    sr = new StringReader("list: [ int : 10, string: \"hel\\nlo\", utc : 100000 ]");
    parser = new SonParserImpl(sr);
    val = parser.sonList();
    Assert.assertThat(val.getType(), is(SonType.LIST));
    Assert.assertThat(val.listValue().size(), is(3));
    Assert.assertThat(val.listValue().get(0).intValue(), is(10));
    Assert.assertThat(val.listValue().get(1).stringValue(), is("hel\nlo"));
    Assert.assertThat(val.listValue().get(2).dateValue().utcMillis(), is(100000L));

  }

  @Test
  public void testSimpleMap() throws ParseException {
    SonParser parser = new SonParser();
    StringReader sr = new StringReader("{ \"fo\\\"o\": int : 10 }");
    parser.use(sr);
    MutableSonMap val = parser.map();
    Assert.assertThat(val.size(), is(1));
    Assert.assertThat(val.get("fo\"o").intValue(), is(10));

    sr = new StringReader("{ \"fo\\no\": int : 10, \"bar\" : string: \"hello\" }");
    parser.use(sr);
    val = parser.map();
    Assert.assertThat(val.size(), is(2));
    Assert.assertThat(val.get("fo\no").intValue(), is(10));
    Assert.assertThat(val.get("bar").stringValue(), is("hello"));

    sr = new StringReader("map: { \"foo\": int : 10 }");
    parser.use(sr);
    val = parser.map();
    Assert.assertThat(val.size(), is(1));
    Assert.assertThat(val.get("foo").intValue(), is(10));

    sr = new StringReader("map: { 'foo' : int : 10 }");
    parser.use(sr);
    val = parser.map();
    Assert.assertThat(val.size(), is(1));
    Assert.assertThat(val.get("foo").intValue(), is(10));

    sr = new StringReader("map: { \"fo\\\\o\": int : 10, \"bar\" : string: \"hello\" }");
    parser.use(sr);
    val = parser.map();
    Assert.assertThat(val.size(), is(2));
    Assert.assertThat(val.get("fo\\o").intValue(), is(10));
    Assert.assertThat(val.get("bar").stringValue(), is("hello"));
  }

  @Test
  public void testSimpleValues() throws ParseException {

    StringReader sr = new StringReader("int : 10");
    SonParserImpl parser = new SonParserImpl(sr);
    MutableSonValue val = parser.sonValue();
    Assert.assertThat(val.getType(), Matchers.is(SonType.INT));
    Assert.assertThat(val.getValue(), is(10));

    sr = new StringReader("byte : 10");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.BYTE));
    Assert.assertThat(val.getValue(), is((byte) 10));

    sr = new StringReader("short : 10");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.SHORT));
    Assert.assertThat(val.getValue(), is((short) 10));

    sr = new StringReader("long : 10l");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.LONG));
    Assert.assertThat(val.getValue(), is(10L));

    sr = new StringReader("long : 10L");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.LONG));
    Assert.assertThat(val.getValue(), is(10L));

    sr = new StringReader("long : 10");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.LONG));
    Assert.assertThat(val.getValue(), is(10L));

    sr = new StringReader("10");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.LONG));
    Assert.assertThat(val.getValue(), is(10L));

    sr = new StringReader("10L");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.LONG));
    Assert.assertThat(val.getValue(), is(10L));

    sr = new StringReader("float : 32.2");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.FLOAT));
    Assert.assertThat(val.getValue(), is(32.2f));

    sr = new StringReader("float : 32.2f");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.FLOAT));
    Assert.assertThat(val.getValue(), is(32.2f));

    sr = new StringReader("32.2");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.DOUBLE));
    Assert.assertThat(val.getValue(), is(32.2d));
    //
    sr = new StringReader("32.2f");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.FLOAT));
    Assert.assertThat(val.getValue(), is(32.2f));

    sr = new StringReader("double : 33.5");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.DOUBLE));
    Assert.assertThat(val.getValue(), is(33.5d));

    sr = new StringReader("double : 33.5d");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.DOUBLE));
    Assert.assertThat(val.getValue(), is(33.5d));

    sr = new StringReader("33.5d");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.DOUBLE));
    Assert.assertThat(val.getValue(), is(33.5d));

    sr = new StringReader("char : 'f'");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.CHAR));
    Assert.assertThat(val.getValue(), is('f'));

    sr = new StringReader("'f'");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.CHAR));
    Assert.assertThat(val.getValue(), is('f'));

    sr = new StringReader("string : \"go first\"");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.STRING));
    Assert.assertThat(val.getValue(), is("go first"));

    sr = new StringReader("\"go first\"");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.STRING));
    Assert.assertThat(val.getValue(), is("go first"));

    sr = new StringReader("bool : true");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.BOOL));
    Assert.assertThat(val.boolValue(), is(true));

    sr = new StringReader("true");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.BOOL));
    Assert.assertThat(val.boolValue(), is(true));

    sr = new StringReader("utc : 10000000");
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.DATE));
    Assert.assertThat(val.dateValue().utcMillis(), is(10000000L));

    String p = Base64.getEncoder().encodeToString(new byte[] { 1, 3, 5, 7 });
    sr = new StringReader("bytes : 2# " + p);
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.BYTES));
    Assert.assertThat(val.bytesValue().asArray()[0], is((byte) 1));
    Assert.assertThat(val.bytesValue().asArray()[1], is((byte) 3));
    Assert.assertThat(val.bytesValue().asArray()[2], is((byte) 5));
    Assert.assertThat(val.bytesValue().asArray()[3], is((byte) 7));
    Assert.assertThat(val.bytesValue().getSignifier(), is((byte) 2));

    p = Base64.getEncoder().encodeToString(new byte[0]);
    sr = new StringReader("bytes : " + p);
    parser = new SonParserImpl(sr);
    val = parser.sonValue();
    Assert.assertThat(val.getType(), is(SonType.BYTES));
    Assert.assertThat(val.bytesValue().asArray().length, is(0));
    Assert.assertThat(val.bytesValue().getSignifier(), is((byte) 0));
  }

  @Test
  public void testUUIDParse() throws ParseException {
    StringWriter sw = new StringWriter();
    MutableSonMap m = Son.writeableMap();
    m.put("u", UUID.randomUUID());
    Son.SONPrinters.SON.pretty().printMap(m, sw);

    String s = sw.toString();
    //    System.out.println(s);
    SonParser parser = new SonParser().use(s);
    MutableSonMap m2 = parser.map();
    //    System.out.println(Son.SONPrinters.SON.pretty().print(m2));
    Assert.assertTrue(m2.deepEquals(m));
  }

  @Test
  public void testXMAS() throws IOException {
    final List<MutableSonMap> maps = new ArrayList<>();
    try (InputStream is = this.getClass().getResourceAsStream("/xmas-recipes.json")) {
      SonParser parser = Son.parser().use(new InputStreamReader(is));
      parser.stream().forEach(msv -> maps.add(msv.mapValue()));
    }
    Assert.assertThat(maps.size(), is(1617));

    StringWriter sw = new StringWriter();
    for (MutableSonMap m : maps) {
      Son.SONPrinters.SON.pretty().printMap(m, sw);
    }
    List<MutableSonMap>
      maps2 =
      Son.parser()
         .use(new StringReader(sw.toString()))
         .stream()
         .map(MutableSonValue::mapValue)
         .collect(Collectors.toList());

    Assert.assertThat(maps, is(maps2));
  }

  @Test
  public void testDeath() throws IOException, ParseException {
    try (InputStream is = this.getClass().getResourceAsStream("/death.tcson")) {
      SonParser parser = Son.parser().use(new InputStreamReader(is, StandardCharsets.UTF_8));
      MutableSonMap map1 = parser.map();
      MutableSonMap map2 = parser.map();
    }
  }

  @Test
  public void testLargeStringsAndByteArrays() throws Exception {
    // test various sizes of strings and byte arrays
    // include large ones up to ~16 MB (2^24)
    // also include zero length ones (2^0 - 1)
    for (int i = 0; i < 25; i++) {
      int length = (int) Math.pow(2, i) - 1;
      byte[] randomBytes = randomBytes(length);
      String randomBytesAsString = Base64.getEncoder().encodeToString(randomBytes);
      String randomString = randomString(length);
      String randomStringEscaped = escape(randomString);
      try {
        String line = "bytes:" + randomBytesAsString;
        MutableSonValue value = Son.parser().use(line).son();
        assertThat(value.getType(), is(SonType.BYTES));
        assertThat(value.bytesValue().asArray(), is(randomBytes));

        line = "string:\"" + randomStringEscaped + "\"";
        value = Son.parser().use(line).son();
        assertThat(value.getType(), is(SonType.STRING));
        assertThat(value.stringValue(), is(randomString));
      } catch (AssertionError | Exception e) {
        throw new Exception("Failed with seed: " + seed, e);
      }

      // Construct a file with the random strings and byte arrays to validate file parsing

      Path path = temporaryFolder.newFile().toPath();
      String bytesLine = "{\"$key\":long:2,\"$cells\":map:{\"bytes1\":bytes : " + randomBytesAsString + "}}";
      String stringLine = "{\"$key\":long:2,\"$cells\":map:{\"str1\":string : \"" + randomStringEscaped + "\"}}";
      try {
        Files.write(path, Arrays.asList(bytesLine, stringLine));
      } catch (IOException e) {
        throw new AssertionError("Failed with seed: " + seed, e);
      }
      try (Reader reader = Files.newBufferedReader(path)) {
        SonParser parser = Son.parser().use(reader);
        SonMapValue value = parser.map().get("$cells").mapValue().get("bytes1");
        assertThat(value.getType(), is(SonType.BYTES));
        assertThat(value.bytesValue().asArray(), is(randomBytes));
        value = parser.map().get("$cells").mapValue().get("str1");
        assertThat(value.getType(), is(SonType.STRING));
        assertThat(value.stringValue(), is(randomString));
      } catch (AssertionError | Exception e) {
        throw new Exception("Failed with seed: " + seed, e);
      }
    }
  }

  @Test
  public void testRoundTripSpecialCharacters() throws ParseException {
    MutableSonList list = Son.writeableList();
    list.add("abc\"123\"xyz");
    list.add("abc\n123xyz");
    list.add("abc\r123xyz");
    list.add("abc\\123xyz");
    list.add("abc\t123xyz");
    list.add("abc\b123xyz");
    list.add("abc\f123xyz");
    list.add("abc\b123xyz");
    list.add("abc\'123'xyz");
    list.add("abc\u02cb");
    list.add("abc\\/123xyz");
    list.add("\n\r\\\"\"\t\b\f\b\\");
    list.add("{\\\f\n\n\rc\\\\\\n#d\\\"ef\\\"\\/\\n\\t:\\b\\r\\f\\\\\\\'\\\\\"}");
    String p = Son.SONPrinters.SON.printer(false).printList(list);
    MutableSonList roundTripList = Son.parser().use(p).list();
    assertThat(roundTripList.deepEquals(list), is(true));
  }

  private String randomString(int length) {
    StringBuilder sb = new StringBuilder(length);
    int capacity = sb.capacity();
    while (sb.length() < capacity) {
      int codePoint = random.nextInt(1 + Character.MAX_CODE_POINT);
      if (codePoint < Character.MIN_SURROGATE || codePoint > Character.MAX_SURROGATE) {
        sb.appendCodePoint(codePoint);
      }
    }
    return sb.toString();
  }

  private byte[] randomBytes(int length) {
    byte[] bytes = new byte[length];
    random.nextBytes(bytes);
    return bytes;
  }

  private String escape(String s) {
    return s.replaceAll("\\\\|\"", "\\\\$0")
        .replace("\n", "\\n")
        .replace("\r", "\\r");
  }
}