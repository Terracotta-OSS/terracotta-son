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
package com.terracottatech.tcson;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>DateTime class, trimmed to millis accuracy, always UTC.
 * <p>This class holds a java {@link ZonedDateTime} object, but it keeps
 * it as UTC time (the internal ZoneDateTime will be re-expressed
 * in terms of UTC) and it trims granularity to millis.
 * <p>This is .. a debatable design choice, but means the entire time
 * can be encoded in one long, which does help things a bit.
 */
public class UTCMillisDate implements Serializable,Comparable<UTCMillisDate> {
  private static final long serialVersionUID = -1114007623015613648L;
  private final ZonedDateTime ts;

  public UTCMillisDate(ZonedDateTime incoming) {
    Objects.requireNonNull(incoming);
    this.ts = trim(incoming.withZoneSameInstant(ZoneOffset.UTC));
  }


  private ZonedDateTime trim(ZonedDateTime now) {
    return now.truncatedTo(ChronoUnit.MILLIS);
  }

  public UTCMillisDate(long utcMillis) {
    this.ts = ZonedDateTime.ofInstant(Instant.ofEpochMilli(utcMillis), ZoneOffset.UTC);
  }

  public UTCMillisDate() {
    this.ts = trim(ZonedDateTime.now(ZoneOffset.UTC));
  }

  public static UTCMillisDate now() {
    return new UTCMillisDate();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UTCMillisDate date = (UTCMillisDate) o;
    return utcMillis() == date.utcMillis();
  }

  @Override
  public int hashCode() {
    return Objects.hash(utcMillis());
  }

  public Date jdkDate() {
    return new Date(utcMillis());
  }

  public long utcMillis() {
    return TimeUnit.MILLISECONDS.convert(ts.toEpochSecond(), TimeUnit.SECONDS) +
           TimeUnit.MILLISECONDS.convert(ts.getNano(), TimeUnit.NANOSECONDS);
  }

  @Override
  public String toString() {
    String str = ts.format(DateTimeFormatter.ISO_DATE_TIME);
    return "Date{" + "millis=" + utcMillis() + ", date=" + str + '}';
  }

  public ZonedDateTime zoneDate() {
    return ts;
  }

  @Override
  public int compareTo(UTCMillisDate o) {
    return Long.compare(utcMillis(), o.utcMillis());
  }

}
