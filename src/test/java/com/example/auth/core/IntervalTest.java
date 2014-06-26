package com.example.auth.core;

import static com.example.auth.core.Interval.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class IntervalTest {
  @Test
  public void minutesCalculation() throws Exception {
    Interval expectedInterval = minutes(60);
    Interval actualInterval = new Interval(3600000);

    assertThat(expectedInterval, is(actualInterval));
  }
}