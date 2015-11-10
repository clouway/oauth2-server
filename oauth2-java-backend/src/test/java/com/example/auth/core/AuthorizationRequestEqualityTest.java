package com.example.auth.core;

import com.example.auth.core.authorization.AuthorizationRequest;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthorizationRequestEqualityTest {
  @Test
  public void areEqual() {
    AuthorizationRequest authorizationRequest1 = new AuthorizationRequest("code", "123456", "sessionId");
    AuthorizationRequest authorizationRequest2 = new AuthorizationRequest("code", "123456", "sessionId");

    assertThat(authorizationRequest1, is(authorizationRequest2));
  }

  @Test
  public void areNotEqual() {
    AuthorizationRequest authorizationRequest1 = new AuthorizationRequest("code", "123456", "sessionId");
    AuthorizationRequest authorizationRequest2 = new AuthorizationRequest("mode", "654321", "sessionId");

    assertThat(authorizationRequest1, is(not(authorizationRequest2)));
  }
}