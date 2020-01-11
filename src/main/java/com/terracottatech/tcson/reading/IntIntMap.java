/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson.reading;

/**
 * Inspired by https://github.com/mikvor/hashmapTest IntIntMap4a, with some tweaks.
 * <p>
 * Mainly, return values are boxed to signify misses. This is a perf hit, no question.
 * Maybe? Still better than standard hashmap for ints to ints.
 */
public class IntIntMap {
  private static final int FREE_KEY = 0;
  private final int noValue;
  /** Fill factor, must be between (0 and 1) */
  private final float fillFactor;
  /** Keys and values */
  private int[] data;
  /** Do we have 'free' key in the map? */
  private boolean mapHasFreeKey;
  /** Value of 'free' key */
  private int freeValue;
  /** We will resize a map once it reaches this size */
  private int threshold;
  /** Current map size */
  private int size;

  /** Mask to calculate the original position */
  private int mask;
  private int maskTimes2;

  public IntIntMap(int noValue) {
    this(noValue, 32);
  }

  public IntIntMap(int noValue, int size) {
    this(noValue, size, 0.75f);
  }

  public IntIntMap(final int noValue, final int size, final float fillFactor) {
    this.noValue = noValue;
    if (fillFactor <= 0 || fillFactor >= 1) {
      throw new IllegalArgumentException();
    }
    if (size <= 0) {
      throw new IllegalArgumentException();
    }
    final int capacity = (int) nextPowerOfTwo(size);
    this.mask = capacity - 1;
    this.maskTimes2 = capacity * 2 - 1;
    this.fillFactor = fillFactor;

    this.data = new int[capacity * 2];
    this.threshold = (int) (capacity * fillFactor);
  }

  public static long nextPowerOfTwo(int i) {
    long tmp = Integer.highestOneBit(i);
    if (tmp != i) {
      tmp = tmp << 1;
    }
    return tmp;
  }

  public int get(final int key) {
    int ptr = (stirHash(key) & mask) << 1;

    if (key == FREE_KEY) {
      return mapHasFreeKey ? freeValue : noValue;
    }

    int k = data[ptr];

    if (k == FREE_KEY) {
      return noValue;  //end of chain already
    }
    if (k == key) {
      return data[ptr + 1];
    }

    while (true) {
      ptr = (ptr + 2) & maskTimes2; //that's next index
      k = data[ptr];
      if (k == FREE_KEY) {
        return noValue;
      }
      if (k == key) {
        return data[ptr + 1];
      }
    }
  }

  private static int stirHash(int key) {
    int c2 = 0x27d4eb2d; // a prime or an odd constant
    key = (key ^ 61) ^ (key >>> 16);
    key = key + (key << 3);
    key = key ^ (key >>> 4);
    key = key * c2;
    key = key ^ (key >>> 15);
    return key;
  }

  public int put(final int key, final int value) {
    if (key == FREE_KEY) {
      final int ret = freeValue;
      if (!mapHasFreeKey) {
        ++size;
      }
      mapHasFreeKey = true;
      freeValue = value;
      return ret;
    }

    int ptr = (stirHash(key) & mask) << 1;
    int k = data[ptr];
    if (k == FREE_KEY) {
      data[ptr] = key;
      data[ptr + 1] = value;
      if (size >= threshold) {
        rehash(data.length * 2); //size is set inside
      } else {
        ++size;
      }
      return noValue;
    } else if (k == key) {
      final int ret = data[ptr + 1];
      data[ptr + 1] = value;
      return ret;
    }

    while (true) {
      ptr = (ptr + 2) & maskTimes2; //that's next index calculation
      k = data[ptr];
      if (k == FREE_KEY) {
        data[ptr] = key;
        data[ptr + 1] = value;
        if (size >= threshold) {
          rehash(data.length * 2); //size is set inside
        } else {
          ++size;
        }
        return noValue;
      } else if (k == key) {
        final int ret = data[ptr + 1];
        data[ptr + 1] = value;
        return ret;
      }
    }
  }

  private void rehash(final int newCapacity) {
    Float p = (newCapacity / (2 * fillFactor));
    threshold = p.intValue();
    mask = newCapacity / 2 - 1;
    maskTimes2 = newCapacity - 1;

    final int oldCapacity = data.length;
    final int[] oldData = data;

    data = new int[newCapacity];
    size = mapHasFreeKey ? 1 : 0;

    for (int i = 0; i < oldCapacity; i += 2) {
      final int oldKey = oldData[i];
      if (oldKey != FREE_KEY) {
        put(oldKey, oldData[i + 1]);
      }
    }
  }

  public int remove(final int key) {
    if (key == FREE_KEY) {
      if (!mapHasFreeKey) {
        return noValue;
      }
      mapHasFreeKey = false;
      --size;
      return freeValue; //value is not cleaned
    }

    int ptr = (stirHash(key) & mask) << 1;
    int k = data[ptr];
    if (k == key) {
      final int res = data[ptr + 1];
      shiftKeys(ptr);
      --size;
      return res;
    } else if (k == FREE_KEY) {
      return noValue;  //end of chain already
    }
    while (true) {
      ptr = (ptr + 2) & maskTimes2; // that's next index calculation
      k = data[ptr];
      if (k == key) {
        final int res = data[ptr + 1];
        shiftKeys(ptr);
        --size;
        return res;
      } else if (k == FREE_KEY) {
        return noValue;
      }
    }
  }

  private int shiftKeys(int pos) {
    // Shift entries with the same hash.
    int last, slot;
    int k;
    final int[] data = this.data;
    while (true) {
      pos = ((last = pos) + 2) & maskTimes2;
      while (true) {
        if ((k = data[pos]) == FREE_KEY) {
          data[last] = FREE_KEY;
          return last;
        }
        slot = (stirHash(k) & mask) << 1; //calculate the starting slot for the current key
        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
          break;
        }
        pos = (pos + 2) & maskTimes2; //go to the next entry
      }
      data[last] = k;
      data[last + 1] = data[pos + 1];
    }
  }

  public int size() {
    return size;
  }

}
