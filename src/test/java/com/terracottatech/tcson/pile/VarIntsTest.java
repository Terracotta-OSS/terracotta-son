/*
 * Copyright IBM Corp. 2020, 2025
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
package com.terracottatech.tcson.pile;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;

public class VarIntsTest {

  @Test
  public void testZigZag() {
    byte[] seed = SecureRandom.getSeed(4);
    System.out.println("Seed: " + Arrays.toString(seed));
    SecureRandom r = new SecureRandom(seed);

    final int OPS = 1000000;

    ByteBuffer use = ByteBuffer.allocate(10);
    for (int i = 0; i < OPS; i++) {
      long l = r.nextLong();
      use.clear();
      VarInts.zigzagEncode(use, l);
      use.flip();
      long l1 = VarInts.zigzagDecode(use.slice());
      long l2 = VarInts.zigzagDecode(use.slice(), 0);
      Assert.assertThat(l, is(l1));
      Assert.assertThat(l, is(l2));
    }
  }
}