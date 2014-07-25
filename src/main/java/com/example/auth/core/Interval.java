package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Interval {
  public Integer milliseconds; //It contains the interval in milliseconds

  public Interval() {
  }

  public Interval(Integer milliseconds) {
    this.milliseconds = milliseconds;
  }

  public static Interval minutes(Integer minutes) {
    return new Interval(minutes * 60 * 1000);
  }

  public static Interval hours(Integer hours) {
    return new Interval(hours * 60 * 60 * 1000);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Interval interval = (Interval) o;

    if (milliseconds != null ? !milliseconds.equals(interval.milliseconds) : interval.milliseconds != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    return milliseconds != null ? milliseconds.hashCode() : 0;
  }
}