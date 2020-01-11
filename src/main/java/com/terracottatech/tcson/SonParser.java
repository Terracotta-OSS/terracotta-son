/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
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
