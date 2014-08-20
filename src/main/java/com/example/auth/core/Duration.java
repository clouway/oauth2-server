package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Duration {
  public Long milliseconds; //It contains the interval in milliseconds

  public Duration() {
  }

  public Duration(Long milliseconds) {
    this.milliseconds = milliseconds;
  }

  public static Duration minutes(Integer minutes) {
    return new Duration(minutes * 60 * 1000l);
  }

  public static Duration hours(Integer hours) {
    return new Duration(hours * 60 * 60 * 1000l);
  }

  public static Duration seconds(Long seconds) {
    return new Duration(seconds * 1000l);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Duration duration = (Duration) o;

    if (milliseconds != null ? !milliseconds.equals(duration.milliseconds) : duration.milliseconds != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    return milliseconds != null ? milliseconds.hashCode() : 0;
  }
}