package com.clouway.oauth2.exampleapp;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class ErrorResponseDTOEqualityTest {
  @Test
  public void areEqual() {
    ErrorResponseDTO errorResponse1 = new ErrorResponseDTO("code", "description");
    ErrorResponseDTO errorResponse2 = new ErrorResponseDTO("code", "description");

    assertThat(errorResponse1, is(errorResponse2));
  }

  @Test
  public void areNotEqual() {
    ErrorResponseDTO errorResponse1 = new ErrorResponseDTO("code1", "description1");
    ErrorResponseDTO errorResponse2 = new ErrorResponseDTO("code2", "description2");

    assertThat(errorResponse1, is(not(errorResponse2)));
  }
}