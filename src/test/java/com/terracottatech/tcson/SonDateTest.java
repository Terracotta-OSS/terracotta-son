/*
 * Copyright (c) 2011-2019 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 */
package com.terracottatech.tcson;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.is;

public class SonDateTest {
  @Test
  public void testTrimming() throws InterruptedException {
    for (int i = 0; i < 10; i++) {
      UTCMillisDate sd = new UTCMillisDate();
      long nanos = sd.zoneDate().getNano();
      Assert.assertThat((nanos / 1000000) * 1000000, is(nanos));
      Thread.sleep(1);
    }
  }

  @Test
  public void testUTC() {
    ZonedDateTime son = new UTCMillisDate().zoneDate();
    Assert.assertThat(son.getOffset(), is(ZoneOffset.UTC));
    if (!ZonedDateTime.now().getOffset().equals(ZoneOffset.UTC)) {
      Assert.assertThat(son.getHour(), Matchers.not(ZonedDateTime.now().getHour()));
    }
  }
}