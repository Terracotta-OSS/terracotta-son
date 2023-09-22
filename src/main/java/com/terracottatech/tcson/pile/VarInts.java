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
package com.terracottatech.tcson.pile;

import java.nio.ByteBuffer;

/**
 * VLE positive integers and ZigZag encoded integers. ZZ are more useful,
 * because they handle negative numbers nicely, with the size of the
 * compressed integer proportional to the absolute value of the integer.
 * Hence, 0 is small, then [1, -1] are next, then [2, -2] etc. They are used
 * extensively in protobufs.
 */
public class VarInts {

  public static int varEncode(ByteBuffer dest, long v) {
    int cnt = 0;
    while (true) {
      cnt++;
      int bits = (int) (v & 0x7f);
      v >>>= 7;
      if (v == 0) {
        dest.put((byte) bits);
        return cnt;
      }
      dest.put((byte) (bits | 0x80));
    }
  }

  public static long varDecode(ByteBuffer src) {
    long ret = 0;
    int shift = 0;
    for (; ; ) {
      byte b = src.get();
      if (b >= 0) {
        return b << shift | ret;
      } else {
        b = (byte) (b & 0x7f);
        ret = (b << shift) | ret;
      }
      shift = shift + 7;
    }
  }

  public static long varDecode(ByteBuffer src, int pos) {
    long ret = 0;
    int shift = 0;
    for (; ; ) {
      byte b = src.get(pos++);
      if (b >= 0) {
        return b << shift | ret;
      } else {
        b = (byte) (b & 0x7f);
        ret = (b << shift) | ret;
      }
      shift = shift + 7;
    }
  }

  public static int zigzagEncode(ByteBuffer dest, long v) {
    long encoded = zigzag_encode(v);
    int usefulBits = 64 - Long.numberOfLeadingZeros(encoded);
    int cnt = 0;
    for (; ; ) {
      int val = (int) (encoded & 0x7f);
      encoded = encoded >>> 7;
      usefulBits = usefulBits - 7;
      if (usefulBits <= 0) {
        dest.put((byte) val);
        return cnt + 1;
      } else {
        dest.put((byte) (val | 0b10000000));
        cnt++;
      }
    }
  }

  private static long zigzag_encode(long i) {
    return (i >> 63L) ^ (i << 1L);
  }

  public static long zigzagDecode(ByteBuffer dest) {
    long value = 0L;
    for (int shift = 0; shift < 64; shift += 7) {
      long v = dest.get();
      value = value | ((v & 0x7f) << shift);
      if (v >= 0) {
        return zigzag_decode(value);
      }
    }
    throw new IllegalStateException();
  }

  private static long zigzag_decode(long n) {
    return (n >>> 1) ^ -(n & 1);
  }

  public static long zigzagDecode(ByteBuffer dest, int pos) {
    long value = 0L;
    for (int shift = 0; shift < 64; shift += 7) {
      long v = dest.get(pos++);
      value = value | ((v & 0x7f) << shift);
      if (v >= 0) {
        return zigzag_decode(value);
      }
    }
    throw new IllegalStateException();
  }
}
