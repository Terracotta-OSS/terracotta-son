/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
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
