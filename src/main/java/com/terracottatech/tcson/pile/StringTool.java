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
package com.terracottatech.tcson.pile;

import java.io.UTFDataFormatException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;

/**
 * A utility class for encoding and decoding {@code String} instances.
 * AKA "Clifford's magic String class".
 * <p>
 * The encoding supported by this class is a modified UTF encoding based on the encoding
 * described in
 * <a href="http://docs.oracle.com/javase/8/docs/api/java/io/DataInput.html#modified-utf-8">Modified UTF-8</a>.
 * <p>
 * The methods in this class do <b>not</b> handle {@code null} {@code String} values.
 * <p>
 * The following encoded forms are supported:
 * <pre>{@code
 *     byte 0: format designator
 *         0x00: encoded value <= 65535 bytes
 *             byte 1: unsigned short byte length of encoding
 *             byte 3: modified UTF-8 encoded String (< 64K in length)
 *         0x01: encoded value > 65565 bytes, encoded length < String.length * 2
 *             byte 1: long byte length of encoding
 *             byte 9: modified UTF-8 encoded String
 *         0x02: encoded value > 65535 bytes, encoded length >= String.length * 2
 *             byte 1: long byte length of encoding
 *             byte 9: non-encoded String.toCharArray (effectively)
 *         0x03: String.length == encoded value length <= 65535 bytes
 *             byte 1: unsigned short byte length of encoding
 *             byte 3: modified UTF-8 encoded String (< 64K in length)
 *         0x04: String.length == encoded value length > 65535 bytes
 *             byte 1: int byte length of encoding
 *             byte 5: modified UTF-8 encoded String (>= 64K in length)
 * }</pre>
 * Format designators {@code 0x03} and {@code 0x04} support encode/decode optimizations -- when the
 * encoded length is equal to the original string length, the string is composed only of 7-bit ASCII
 * characters which encode by simply casting to {@code byte}.
 *
 * @author Clifford W. Johnson
 * Although the format permits lengths greater than {@code Integer.MAX_VALUE}, present
 * {@code ByteBuffer} capacity is limited to {@code Integer.MAX_VALUE} so the maximum supported
 * length for an encoded {@code String} is less than {@code Integer.MAX_VALUE}.
 */
// PERFORMANCE NOTE:  The methods in this class are organized and sized to promote JIT inlining.
public final class StringTool {
  /**
   * Largest character slice from a {@code String} being "put".
   */
  private static final int MAX_SLICE_LENGTH = 512;

  /**
   * Private niladic constructor to prevent instantiation.
   */
  private StringTool() {
  }

  /**
   * Decodes a modified UTF-8 encoding of a {@code String} beginning at the current position of the
   * {@code ByteBuffer} provided.  For encodings larger than 65,535 bytes, this method begins with a
   * 65,535 character buffer and enlarges it as needed to hold the decoded value.
   *
   * @param buffer the {@code ByteBuffer} containing the encoded bytes
   * @param encodedLength the byte length of the encoding
   * @return a decoded {@code String}
   * @throws UTFDataFormatException if an error is encountered while decoding the UTF value
   * @throws BufferUnderflowException if {@code buffer} is too small to hold the declared UTF value
   */
  public static String decodeString(final ByteBuffer buffer,
                                    final long encodedLength) throws UTFDataFormatException, BufferUnderflowException {

    final int initialArrayLength = (int) encodedLength;
    char[] chars = new char[initialArrayLength];
    int charIndex = -1;
    long remaining = encodedLength;
    while (remaining > 0) {
      charIndex++;

      /*
       * If the decoding array has reached it's capacity, enlarge it.
       */
      if (charIndex == chars.length) {
        chars = enlargeChars(encodedLength, chars);
      }

      int b = Byte.toUnsignedInt(buffer.get());
      int f = b >>> 4;
      if (f < 0x08) {
        // Single-byte character
        chars[charIndex] = (char) b;
        remaining -= 1;
      } else if (f == 0x0E) {
        // Three-byte character
        chars[charIndex] = (char) ((b & 0x0F) << 12 | (buffer.get() & 0x3F) << 6 | buffer.get() & 0x3F);
        remaining -= 3;
      } else if (f >= 0x0C) {
        // Two-byte character
        chars[charIndex] = (char) ((b & 0x1F) << 6 | buffer.get() & 0x3F);
        remaining -= 2;
      } else {
        throw new UTFDataFormatException(String.format("Illegal element: %02x", b));
      }
    }

    if (remaining != 0) {
      throw new UTFDataFormatException(String.format("Decoding string: %d bytes remaining after decoding", remaining));
    }

    return new String(chars, 0, 1 + charIndex);
  }

  /**
   * Copies a {@code code} array into one increased in size by 0.25 * encodedLength.
   *
   * @param encodedLength the encoded length
   * @param chars the source {@code char[]}
   * @return a new, larger {@code char[]} containing the content from {@code chars}
   */
  private static char[] enlargeChars(final long encodedLength, final char[] chars) {
    long newLength = (long) chars.length + (int) ((encodedLength + 3) / 4);
    if (newLength != (int) newLength) {
      // arithmetic overflow
      throw new OutOfMemoryError();
    }
    return Arrays.copyOf(chars, (int) newLength);
  }

  /**
   * Appends the <i>modified</i> UTF-8 representation of a {@code String} to the {@code ByteBuffer} provided.
   * The buffer's position is advanced by the number of bytes required by the modified UTF-8 representation.
   *
   * @param buffer the {@code ByteBuffer} into which {@code str} is encoded
   * @param str the {@code String} to encode
   * @param strLength the length of {@code str}
   * @throws BufferOverflowException if {@code buffer} is too small for the UTF-encoded {@code str}
   * @throws ReadOnlyBufferException if {@code buffer} is read-only
   */
  public static void putEncoded(final ByteBuffer buffer,
                                final String str,
                                final int strLength) throws BufferOverflowException, ReadOnlyBufferException {

    final char[] slice = new char[MAX_SLICE_LENGTH];
    int sz = 0;
    for (int offset = 0; offset < strLength; offset += MAX_SLICE_LENGTH) {
      final int sliceLength = Math.min(MAX_SLICE_LENGTH, strLength - offset);
      str.getChars(offset, offset + sliceLength, slice, 0);
      for (int i = 0; i < sliceLength; i++) {
        final char c = slice[i];
        if (c <= '\u007F' && c != '\u0000') {
          buffer.put((byte) c);
          sz++;
        } else if (c <= '\u07FF') {
          buffer.put((byte) (0xC0 | c >>> 6)).put((byte) (0x80 | (c & 0x3F)));
          sz = sz + 2;
        } else {
          buffer.put((byte) (0xE0 | c >>> 12)).put((byte) (0x80 | ((c >>> 6) & 0x3F))).put((byte) (0x80 | (c & 0x3F)));
          sz = sz + 3;
        }
      }
    }
  }

  /**
   * Return the worst case size needed to store a string.
   *
   * @param str string
   * @return size
   */
  public static int worstCaseByteArraySize(String str) {
    return str.length() * 4 + 8;
  }
}
