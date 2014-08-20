package com.example.auth.core;

import org.junit.Test;

import static com.example.auth.core.Duration.hours;
import static com.example.auth.core.Duration.minutes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class DurationTest {
  @Test
  public void minutesCalculation() throws Exception {
    Duration expectedDuration = minutes(60);
    Duration actualDuration = new Duration(3600000l);

    assertThat(expectedDuration, is(actualDuration));
  }

  @Test
  public void hoursCalculation() throws Exception {
    Duration expectedDuration = hours(24);
    Duration actualDuration = new Duration(86400000l);

    assertThat(expectedDuration, is(actualDuration));
  }
}