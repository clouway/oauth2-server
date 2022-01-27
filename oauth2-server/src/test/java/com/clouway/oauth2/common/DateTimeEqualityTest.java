package com.clouway.oauth2.common;

import com.clouway.oauth2.common.DateTime;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class DateTimeEqualityTest {

  @Test
  public void areEqual() {
    assertThat(new DateTime(1L), is(equalTo(new DateTime(1L))));
  }

  @Test
  public void areNotEqual() {
    assertThat(new DateTime(1L), is(not(equalTo(new DateTime(2L)))));
  }

}