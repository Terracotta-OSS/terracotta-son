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
import java.nio.ByteOrder;

public class ManagedBuffer {
  private int initial;
  private int max;
  private ByteBuffer buffer;

  public ManagedBuffer() {
    this(1024);
  }

  public ManagedBuffer(int initial) {
    this(ByteBuffer.allocate(initial));
  }

  public ManagedBuffer(ByteBuffer b) {
    reset(b);
  }

  public void reset(ByteBuffer b) {
    this.buffer = b;
    this.initial = this.max = b.remaining();
  }

  public void ensureRemaining(int need) {
    if (buffer.remaining() < need) {
      int total = buffer.capacity() + Math.max(2048, need);
      ByteBuffer tmp = ByteBuffer.allocate(total);
      this.buffer.order(ByteOrder.LITTLE_ENDIAN);
      max = total;
      buffer.flip();
      tmp.put(buffer);
      buffer = tmp;
    }
  }

  public ByteBuffer getBuffer() {
    return buffer;
  }

  public void reset() {
    if (shouldResizeDown()) {
      this.buffer = ByteBuffer.allocate(max);
    }
    this.buffer.clear();
  }

  private boolean shouldResizeDown() {
    if (buffer.remaining() < (max >>> 1)) {
      int amt = buffer.remaining();
      // next power of two, or the initial
      max = Math.max(initial, (1 << (amt == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(amt - 1))));
      return true;
    }
    return false;
  }

}
