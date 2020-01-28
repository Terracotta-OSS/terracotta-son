/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.query;

import com.terracottatech.tcson.parser.FieldReference;
import com.terracottatech.tcson.parser.query.ParseException;
import com.terracottatech.tcson.parser.query.SonQueryParser;

import java.io.StringReader;
import java.util.List;

/**
 * Dot notation parser. Thread safe.
 */
public class SonDotParser {
  private final SonQueryParser parser = new SonQueryParser(new StringReader(""));

  public SonDotTraversal parse(String dotSpec) throws ParseException {
    SonDotTraversal ret = parse(new StringReader(dotSpec));
    return ret;
  }

  public SonDotTraversal parse(StringReader reader) throws ParseException {
    List<FieldReference> spec;
    synchronized (this) {
      parser.ReInit(reader);
      spec = parser.dotterSpec();
    }
    return new SonDotTraversal(spec);
  }
}
