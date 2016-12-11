package com.clouway.oauth2;

import com.clouway.oauth2.authorization.Authorization;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class AuthorizationEqualityTest {
  @Test
  public void areEqual() {
    Authorization authorization1 = new Authorization("code", "id", "identityId", "123456", Collections.singleton("scope1"), Collections.singleton("redirectURI"));
    Authorization authorization2 = new Authorization("code", "id", "identityId", "123456", Collections.singleton("scope1"), Collections.singleton("redirectURI"));

    assertThat(authorization1, is(authorization2));
  }

  @Test
  public void idIsNotMatching() {
    Authorization authorization1 = new Authorization("code", "id1", "identityId", "123456", Collections.singleton("scope1"), Collections.singleton("redirectURI"));
    Authorization authorization2 = new Authorization("code", "id2", "identityId", "123456", Collections.singleton("scope1"), Collections.singleton("redirectURI"));

    assertThat(authorization1, is(not(authorization2)));
  }

  @Test
  public void scopesAreNotMatching() {
    Authorization authorization1 = new Authorization("code", "id1", "identityId", "123456", Collections.singleton("scope1"), Collections.singleton("redirectURI"));
    Authorization authorization2 = new Authorization("code", "id1", "identityId", "123456", Collections.singleton("scope2"), Collections.singleton("redirectURI"));

    assertThat(authorization1, is(not(authorization2)));
  }

  @Test
  public void redirectURIsAreNotMatching() throws Exception {
    Authorization authorization1 = new Authorization("code", "id1", "identityId", "123456", Collections.singleton("scope1"), Collections.singleton("redirectURI"));
    Authorization authorization2 = new Authorization("code", "id1", "identityId", "123456", Collections.singleton("scope1"), Collections.singleton("redirectURI2"));

    assertThat(authorization1, is(not(authorization2)));
  }
}
