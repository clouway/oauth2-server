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
    TokenRequest request1 = new TokenRequest("grant_type", "code", "client_id", "client_secret");
    TokenRequest request2 = new TokenRequest("grant_type", "code", "client_id", "client_secret");

    assertThat(request1, is(request2));
  }

  @Test
  public void areNotEqual() {
    TokenRequest request1 = new TokenRequest("grant_type1", "code1", "client_id1", "client_secret1");
    TokenRequest request2 = new TokenRequest("grant_type2", "code2", "client_id2", "client_secret2");

    assertThat(request1, is(not(request2)));
  }
}