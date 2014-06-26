package com.example.auth.core;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class IntervalEqualityTest {
  @Test
  public void areEqual() {
    Interval interval1 = new Interval(1000);
    Interval interval2 = new Interval(1000);

    assertThat(interval1, is(interval2));
  }

  @Test
  public void areNotEqual() {
    Interval interval1 = new Interval(1000);
    Interval interval2 = new Interval(2000);

    assertThat(interval1, is(not(interval2)));
  }
}