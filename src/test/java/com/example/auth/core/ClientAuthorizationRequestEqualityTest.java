package com.example.auth.core;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class ClientAuthorizationRequestEqualityTest {
  @Test
  public void areEqual() {
    ClientAuthorizationRequest clientAuthorizationRequest1 = new ClientAuthorizationRequest("code", "id", "123456", "redirectURI");
    ClientAuthorizationRequest clientAuthorizationRequest2 = new ClientAuthorizationRequest("code", "id", "123456", "redirectURI");

    assertThat(clientAuthorizationRequest1, is(clientAuthorizationRequest2));
  }

  @Test
  public void areNotEqual() {
    ClientAuthorizationRequest clientAuthorizationRequest1 = new ClientAuthorizationRequest("code1", "id1", "123456", "redirectURI1");
    ClientAuthorizationRequest clientAuthorizationRequest2 = new ClientAuthorizationRequest("code2", "id2", "654321", "redirectURI2");

    assertThat(clientAuthorizationRequest1, is(not(clientAuthorizationRequest2)));
  }
}
