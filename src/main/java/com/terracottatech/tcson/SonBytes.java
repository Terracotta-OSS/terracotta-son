/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Objects;

/**
 * Important to note that the ByteBuffer held here is a *view* into
 * the underlying buffer, in the case of a readable map.
 */
public class SonBytes implements Serializable {
  private static final long serialVersionUID = -3339264057019526820L;
  private final ByteBuffer buffer;
  private final byte signifier;

  public SonBytes(byte signifier, ByteBuffer buffer) {
    this.buffer = buffer.asReadOnlyBuffer();
    this.signifier = signifier;
  }

  public SonBytes(byte signifier, byte[] b) {
    this(signifier, b, 0, b.length);
  }

  public SonBytes(byte signifier, byte[] b, int i, int length) {
    this.signifier = signifier;
    this.buffer = ByteBuffer.wrap(b, i, length).asReadOnlyBuffer();
  }

  public byte[] asArray() {
    byte[] b = new byte[buffer.remaining()];
    buffer.slice().get(b);
    return b;
  }

  public SonBytes dup() {
    ByteBuffer b = ByteBuffer.allocate(buffer.remaining());
    b.put(buffer.slice());
    b.clear();
    return new SonBytes(signifier, b);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SonBytes bytes = (SonBytes) o;
    return signifier == bytes.signifier && buffer.equals(bytes.buffer);
  }

  public ByteBuffer getBuffer() {
    return buffer;
  }

  public byte getSignifier() {
    return signifier;
  }

  @Override
  public int hashCode() {
    return Objects.hash(buffer, signifier);
  }

  @Override
  public String toString() {
    return "SonBytes{" + "signifier=" + signifier + toBase64(buffer.slice()) + '}';
  }

  public static String toBase64(ByteBuffer obj) {
    byte[] b = new byte[obj.remaining()];
    obj.slice().get(b);
    return Base64.getEncoder().encodeToString(b);
  }
}
