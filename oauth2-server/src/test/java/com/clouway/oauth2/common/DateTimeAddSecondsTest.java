package com.clouway.oauth2.common;

import org.junit.Test;

import static com.clouway.oauth2.common.CalendarUtil.newTime;
import static com.clouway.oauth2.common.CommonMatchers.timeIsAt;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class DateTimeAddSecondsTest {

  @Test
  public void add30Seconds() {
    assertThat(new DateTime(newTime(23, 45, 10)).plusSeconds(30), timeIsAt(23, 45, 40));
  }

  @Test
  public void add120Seconds() {
    assertThat(new DateTime(newTime(23, 45, 10)).plusSeconds(120), timeIsAt(23, 47, 10));
  }

  @Test
  public void subSecondsByAddingNegative() {
    assertThat(new DateTime(newTime(23, 45, 10)).plusSeconds(-1), timeIsAt(23, 45, 9));
  }
}