/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
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