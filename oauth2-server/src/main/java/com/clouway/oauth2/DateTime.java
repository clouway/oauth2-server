package com.clouway.oauth2;

import java.util.Date;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class DateTime {
  private final Date time;

  public DateTime(Date time) {
    this.time = time;
  }

  public DateTime(Long timeAsMillis) {
    this.time = new Date(timeAsMillis);
  }

  public DateTime() {
    this.time = new Date();
  }

  public boolean after(DateTime date) {
    return time.after(date.asDate());
  }

  public boolean before(DateTime date) {
    return time.before(date.asDate());
  }

  public DateTime plusSeconds(long seconds) {
    return new DateTime(new Date(time.getTime() + 1000 * seconds));
  }

  public DateTime minusSeconds(long seconds) {
    return new DateTime(new Date(time.getTime() - 1000 * seconds));
  }

  @Override
  public String toString() {
    return "DateTime{" + time + '}';
  }

  public Date asDate() {
    return new Date(time.getTime());
  }

  public Long timestamp() {
    return time.getTime();
  }
}
