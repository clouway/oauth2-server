package com.clouway.oauth2;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class DateTimeAddSecondsTest {

  @Test
  public void add30Seconds() {
    DateTime dateTime = new DateTime(newTime(23, 45, 10));
    assertTime(dateTime.plusSeconds(30), 23, 45, 40);
  }

  @Test
  public void add120Seconds() {
    DateTime dateTime = new DateTime(newTime(23, 45, 10));
    assertTime(dateTime.plusSeconds(120), 23, 47, 10);
  }

  @Test
  public void subSecondsByAddingNegative() {
    assertTime(new DateTime(newTime(23, 45, 10)).plusSeconds(-1),23,45,9);
  }

  private void assertTime(DateTime time, int hour, int minute, int second) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(time.asDate());
    assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(equalTo(hour)));
    assertThat(calendar.get(Calendar.MINUTE), is(equalTo(minute)));
    assertThat(calendar.get(Calendar.SECOND), is(equalTo(second)));
  }

  private Date newTime(int hour, int minute, int seconds) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, seconds);
    return calendar.getTime();
  }

}