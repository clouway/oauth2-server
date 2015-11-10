package com.clouway.oauth2;

import com.clouway.oauth2.Duration;
import org.junit.Test;

import static com.clouway.oauth2.Duration.hours;
import static com.clouway.oauth2.Duration.minutes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class DurationTest {
  @Test
  public void minutesCalculation() throws Exception {
    Duration expectedDuration = minutes(60);
    Duration actualDuration = new Duration(3600l);

    assertThat(expectedDuration, is(actualDuration));
  }

  @Test
  public void hoursCalculation() throws Exception {
    Duration expectedDuration = hours(24);
    Duration actualDuration = new Duration(86400l);

    assertThat(expectedDuration, is(actualDuration));
  }
}