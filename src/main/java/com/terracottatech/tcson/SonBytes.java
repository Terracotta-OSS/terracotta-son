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
package com.terracottatech.tcson;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Objects;

/**
 * Important to note that the ByteBuffer held here is a *view* into
 * the underlying buffer, in the case of a readable map.
 */
public class SonBytes implements Serializable, Comparable<SonBytes> {
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

  @Override
  public int compareTo(SonBytes o) {
    int ret = Byte.compare(signifier, o.signifier);
    if (ret == 0) {
      ret = buffer.slice().compareTo(o.buffer.slice());
    }
    return ret;
  }
}
