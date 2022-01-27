package com.clouway.oauth2.common;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class DurationEqualityTest {
  @Test
  public void areEqual() {
    Duration duration1 = new Duration(1000L);
    Duration duration2 = new Duration(1000L);

    assertThat(duration1, is(duration2));
  }

  @Test
  public void areNotEqual() {
    Duration duration1 = new Duration(1000L);
    Duration duration2 = new Duration(2000L);

    assertThat(duration1, is(not(duration2)));
  }
}