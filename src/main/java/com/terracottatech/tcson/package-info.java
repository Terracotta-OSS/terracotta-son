/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
/**
 * <p>This package provides a package to manipulate a strong typed
 * "SON" format. As in JSON, BSON, whatever-SON. This one is just...
 * "SON".
 *
 * <p>Son consists of maps and lists of various types
 * (including maps and lists). See {@link com.terracottatech.tcson.SonType}
 * for the exact types if interested. They include:
 * <ul>
 *   <li>null value - 1 byte storage</li>
 *   <li>boolean - 1 byte storage</li>
 *   <li>byte - 8 bit signed, 1 byte storage</li>
 *   <li>short - 16 bit signed, 2 bytes storage</li>
 *   <li>int - 32 bit signed, 1-5 bytes storage</li>
 *   <li>long - 64 bit signed, 1-9 bytes storage</li>
 *   <li>float - 32 bit float, 4 bytes storage</li>
 *   <li>double - 64 bit signed, 8 bytes storage</li>
 *   <li>byte[] - variable storage. Managed through the {@link com.terracottatech.tcson.SonBytes}
 *   class, which includes a byte array/byte buffer and a 1 byte user defined
 *   signifier to encode the purpose of the byte array. Signifiers less than
 *   zero are reserved for internal use, zero denotes a plain or untyped array,
 *   while 0-127 can be used as an application needs.</li>
 *   <li>String - UTF string, variable storage</li>
 *   <li>char - 2 bytes storage</li>
 *   <li>UTC Date - variable encoded unix epoch millis, 9 bytes storage</li>
 *   <li>UUID - 17 bytes storage</li>
 *   <li>nested map - variable size, obviously</li>
 *   <li>nested list - variable size, obviously</li>
 *  </ul>
 *
 * <p>Maps/Lists are the entry points, map and lists contain any primitive
 * value, plus map or list values.
 *
 * <p>To create a mutable Map, one does this:
 * <pre>mutableMap = {@link com.terracottatech.tcson.Son}.writeableMap(); </pre>
 * This will create a {@link com.terracottatech.tcson.MutableSonMap} which can be
 * manipulated as needed, including nesting map and list values. Once you have
 * constructed your mutable map, you call:
 * <pre>{@code buffer = mutableMap.toBuffer()}</pre>
 * This gives a ByteBuffer encoded representation of this map.
 * Similar patterns are followed for top level Lists.
 *
 * <p>Alternatively, if the map is to be encoding in a streaming fashion,
 * you can use <pre>writer = {@link com.terracottatech.tcson.Son}.streaming();</pre>
 * This creates a {@link com.terracottatech.tcson.writing.SonStreamingMapWriter} for
 * creating a map in a streaming fashion. This allows for a <b>much</b>
 * faster encoding pass as there is no interim POJO representation of the
 * maps and lists. Again, when done, <pre>{@code buf = writer.buffer();}</pre> will
 * return the buffer, although in this case the position() is left as the last
 * written position. flip() is needed. The same pattern holds for top level lists.
 *
 * <p>In any case, once you have an encoded ByteBuffer version, you use
 * <pre>readableMap = {@link com.terracottatech.tcson.Son}.readableMap(buf);</pre>
 * to get a readable version of the map, a {@link com.terracottatech.tcson.ReadableSonMap}
 * object. This version services all it's requests from the underlying
 * ByteBuffer, and hence is very fast at random access. It can also be
 * turned into a mutable map if needed.
 * <pre>readableList = {@link com.terracottatech.tcson.Son}.readableList(buf);</pre>
 * will give you the same access for a top level list.
 *
 * <p>For parsing, you use <pre>parser = {@link com.terracottatech.tcson.Son}.parser();</pre>
 * to construct an instance of {@link com.terracottatech.tcson.SonParser}.
 * This provides parsing of single values or streams of values.
 * It should parse standard JSON with no problem, with integer values
 * becoming Longs, and floating point values becoming Doubles.
 *
 * <p>For printing, use the {@link com.terracottatech.tcson.Son.SONPrinters } enum
 * to access access to various {@link com.terracottatech.tcson.printers.SonPrinter}
 * printer objects, either SON or JSON, in various formats.
 *
 * <p>{@link com.terracottatech.tcson.MutableSonMap}
 * and {@link com.terracottatech.tcson.MutableSonList}
 * objects are thread safe. {@link com.terracottatech.tcson.ReadableSonMap} and
 * {@link com.terracottatech.tcson.ReadableSonList} objects are as well, providing the underlying
 * buffer is not manipulated. {@link com.terracottatech.tcson.SonValue} and it's subclasses
 * are immutable, and therefore thread safe.
 *
 * <p>UTC date time stamps are dealt with via the
 * {@link com.terracottatech.tcson.UTCMillisDate} class.
 * This allows for Java 8 {@link java.time.ZonedDateTime} date/time objects,
 * but the time instant is trimmed to millisecond granularity,
 * and is always held as UTC time.
 */
package com.terracottatech.tcson;