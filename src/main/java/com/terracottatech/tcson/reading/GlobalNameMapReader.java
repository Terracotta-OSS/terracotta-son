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
package com.terracottatech.tcson.reading;

import com.terracottatech.tcson.NameSource;
import com.terracottatech.tcson.pile.Pile;
import com.terracottatech.tcson.pile.PileReader;

import java.util.HashMap;

/**
 * Map of string key name :: key id. Lazily populated on first request.
 */
public class GlobalNameMapReader {
  private final PileReader namePile;
  private final NameSource nameSource;
  private HashMap<String, Integer> namesToId;

  public GlobalNameMapReader(NameSource nameSource, PileReader namePile) {
    this.nameSource = nameSource;
    this.namePile = namePile;
  }

  public NameSource getNameSource() {
    return nameSource;
  }

  public int lookupId(String name) {
    Integer ret = getNamesToId().get(name);
    return ret == null ? -1 : ret;
  }

  public HashMap<String, Integer> getNamesToId() {
    populateNamesToId();
    return namesToId;
  }

  private void populateNamesToId() {
    if (namesToId == null) {
      HashMap<String, Integer> hm = new HashMap<>();
      int nc = namePile.size();
      for (int i = 0; i < nc; i++) {
        String s;
        if (namePile.typeOf(i).equals(Pile.Type.STRING)) {
          s = namePile.str(i);
        } else {
          long p = namePile.int64(i);
          s = nameSource.nameOf(p);
        }
        hm.put(s, i);
      }
      this.namesToId = hm;
    }
  }

  public int size() {
    return namePile.size();
  }

}
