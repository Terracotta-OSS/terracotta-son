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

import com.terracottatech.tcson.mutable.MutableSonValue;
import com.terracottatech.tcson.parser.ParseException;
import com.terracottatech.tcson.parser.SonParserImpl;

import java.io.Reader;
import java.io.StringReader;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SonParser {
  private SonParserImpl impl;

  public SonParser() {
    this(new StringReader(""));
  }

  SonParser(Reader reader) {
    impl = new SonParserImpl(reader);
  }

  SonParser(String s) {
    this(new StringReader(s));
  }

  public MutableSonList list() throws ParseException {
    return impl.nextList();
  }

  public MutableSonMap map() throws ParseException {
    return impl.nextMap();
  }

  public MutableSonValue son() throws ParseException {
    return impl.sonNext();
  }

  public Stream<MutableSonValue> stream() {
    Spliterator<MutableSonValue> split = new Spliterator<MutableSonValue>() {
      @Override
      public int characteristics() {
        return Spliterator.DISTINCT + Spliterator.NONNULL;
      }

      @Override
      public long estimateSize() {
        return Long.MAX_VALUE;
      }

      @Override
      public boolean tryAdvance(Consumer<? super MutableSonValue> action) {
        try {
          MutableSonValue m = impl.sonNext();
          if (m != null) {
            action.accept(m);
            return true;
          }
          return false;
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public Spliterator<MutableSonValue> trySplit() {
        return null;
      }
    };
    return StreamSupport.stream(split, false);
  }

  public MutableSonValue tc() throws ParseException {
    return impl.nextTC();
  }

  public SonParser use(Reader r) {
    impl.ReInit(r);
    return this;
  }

  public SonParser use(String s) {
    impl.ReInit(new StringReader(s));
    return this;
  }

}
