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
      spec = parser.dotSpec();
    }
    return new SonDotTraversal(spec);
  }
}
