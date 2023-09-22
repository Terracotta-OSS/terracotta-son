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
package com.terracottatech.tcson;

import com.terracottatech.tcson.mutable.MutableSonListImpl;
import com.terracottatech.tcson.mutable.MutableSonMapImpl;
import com.terracottatech.tcson.mutable.MutableSonValue;
import com.terracottatech.tcson.pile.ManagedBuffer;
import com.terracottatech.tcson.printers.JSONPrettyPrinter;
import com.terracottatech.tcson.printers.SonPrettyPrinter;
import com.terracottatech.tcson.printers.SonPrinter;
import com.terracottatech.tcson.query.SonDotParser;
import com.terracottatech.tcson.reading.ReadableSonListImpl;
import com.terracottatech.tcson.reading.ReadableSonMapImpl;
import com.terracottatech.tcson.writing.SonStreamingListWriter;
import com.terracottatech.tcson.writing.SonStreamingMapWriter;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Entry point for serializing, deserializing, and building
 * SonMap/Sonlist structures.
 */
public interface Son {
  /**
   * Enum for accessing printers.
   */
  enum SONPrinters {
    /**
     * Default SON printing. Will only use explicit typing for
     * types that require it.
     */
    SON() {
      @Override
      public SonPrinter printer(boolean compact) {
        return new SonPrettyPrinter(compact, false);
      }
    },
    /**
     * Son printng with explicit typing for every value.
     */
    SON_VERBOSE() {
      @Override
      public SonPrinter printer(boolean compact) {
        return new SonPrettyPrinter(compact, true);
      }
    },
    /**
     * Strict JSON. If an illegal value type is encountered
     * (such as Date, Byte, Short, byte[] etc), and exception will
     * be thrown.
     */
    JSON_STRICT() {
      @Override
      public SonPrinter printer(boolean compact) {
        return new JSONPrettyPrinter(this, compact);
      }
    },
    /**
     * Everything which is strictly JSON will be printed, everything else
     * will be skipped. Dangerous.
     */
    JSON_LOSSY() {
      @Override
      public SonPrinter printer(boolean compact) {
        return new JSONPrettyPrinter(this, compact);
      }
    },
    /**
     * Everything will be printed, but things with no JSON
     * equivalent will be turned into strings.
     */
    JSON_EXTENDED() {
      @Override
      public SonPrinter printer(boolean compact) {
        return new JSONPrettyPrinter(this, compact);
      }
    };

    public SonPrinter compact() {
      return printer(true);
    }

    public abstract SonPrinter printer(boolean compact);

    public SonPrinter pretty() {
      return printer(false);
    }
  }

  /**
   * Create a parser for JSON to SON or SON to SON.
   *
   * @return parser
   */
  static SonParser parser() {
    return new SonParser();
  }

  static ReadableSonMap readableMap(ByteBuffer buf) {
    return new ReadableSonMapImpl(null, buf);
  }

  static ReadableSonMap readableMap(NameSource nameSource, ByteBuffer buf) {
    return new ReadableSonMapImpl(nameSource, buf);
  }

  static ReadableSonList readableList(ByteBuffer buf) {
    return new ReadableSonListImpl(null, buf);
  }

  static ReadableSonList readableList(NameSource nameSource, ByteBuffer buf) {
    return new ReadableSonListImpl(nameSource, buf);
  }

  static MutableSonMap writeableMap() {
    return new MutableSonMapImpl();
  }

  static MutableSonList writeableList() {
    return new MutableSonListImpl();
  }

  static SonStreamingMapWriter<Void> streamingMapWriter() {
    return new SonStreamingMapWriter<>();
  }

  static SonStreamingMapWriter<Void> streamingMapWriter(NameSource nameSource) {
    return new SonStreamingMapWriter<>(nameSource);
  }

  static SonStreamingMapWriter<Void> streamingMapWriter(ManagedBuffer buffer) {
    return new SonStreamingMapWriter<>(null, buffer);
  }

  static SonStreamingMapWriter<Void> streamingMapWriter(NameSource nameSource, ManagedBuffer buffer) {
    return new SonStreamingMapWriter<>(nameSource, buffer);
  }

  static SonStreamingListWriter<Void> streamingListWriter() {
    return new SonStreamingListWriter<>(null, new ManagedBuffer(1024));
  }

  static SonStreamingListWriter<Void> streamingListWriter(ManagedBuffer mb) {
    return new SonStreamingListWriter<>(null, mb);
  }

  static SonStreamingListWriter<Void> streamingListWriter(NameSource nameSource) {
    return new SonStreamingListWriter<>(nameSource, new ManagedBuffer(1024));
  }

  static SonStreamingListWriter<Void> streamingListWriter(NameSource nameSource, ManagedBuffer mb) {
    return new SonStreamingListWriter<>(nameSource, mb);
  }

  static SonDotParser dotParser() {
    return new SonDotParser();
  }

  static SonValue of(boolean v) {
    return new MutableSonValue(SonType.BOOL, v);
  }

  static SonValue of(char v) {
    return new MutableSonValue(SonType.CHAR, v);
  }

  static SonValue of(byte v) {
    return new MutableSonValue(SonType.BYTE, v);
  }

  static SonValue of(short v) {
    return new MutableSonValue(SonType.SHORT, v);
  }

  static SonValue of(int v) {
    return new MutableSonValue(SonType.INT, v);
  }

  static SonValue of(long v) {
    return new MutableSonValue(SonType.LONG, v);
  }

  static SonValue of(float v) {
    return new MutableSonValue(SonType.FLOAT, v);
  }

  static SonValue of(double v) {
    return new MutableSonValue(SonType.DOUBLE, v);
  }

  static SonValue of(String v) {
    return new MutableSonValue(SonType.STRING, v);
  }

  static SonValue ofNull() {
    return MutableSonValue.NULL_VALUE;
  }

  static SonValue of(SonBytes bytes) {
    return new MutableSonValue(SonType.BYTES, bytes);
  }

  static SonValue of(UUID v) {
    return new MutableSonValue(SonType.UUID, v);
  }

  static SonValue of(UTCMillisDate v) {
    return new MutableSonValue(SonType.DATE, v);
  }

  static SonValue of(SonMap<?> v) {
    return v.asSonValue();
  }

  static SonValue of(SonList<?> v) {
    return v.asSonValue();
  }

}
