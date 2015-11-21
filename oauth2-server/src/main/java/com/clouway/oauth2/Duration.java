package com.clouway.oauth2;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Duration {
  public Long seconds;

  public Duration() {
  }

  public Duration(Long seconds) {
    this.seconds = seconds;
  }

  public static Duration minutes(Integer minutes) {
    return new Duration(minutes * 60l);
  }

  public static Duration hours(Integer hours) {
    return new Duration(hours * 60 * 60l);
  }

  public static Duration seconds(Long seconds) {
    return new Duration(seconds);
  }

  public Long asMills() {
    return seconds * 1000;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Duration)) return false;

    Duration duration = (Duration) o;

    if (seconds != null ? !seconds.equals(duration.seconds) : duration.seconds != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return seconds != null ? seconds.hashCode() : 0;
  }
}