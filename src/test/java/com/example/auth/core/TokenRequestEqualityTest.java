package com.example.auth.core;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenRequestEqualityTest {
  @Test
  public void areEqual() {
    TokenRequest request1 = new TokenRequest("grant_type", "FeNoMeNa", "password");
    TokenRequest request2 = new TokenRequest("grant_type", "FeNoMeNa", "password");

    assertThat(request1, is(request2));
  }

  @Test
  public void areNotEqual() {
    TokenRequest request1 = new TokenRequest("grant_type", "FeNoMeNa", "password");
    TokenRequest request2 = new TokenRequest("type_grant", "Joro", "motora");

    assertThat(request1, is(not(request2)));
  }
}