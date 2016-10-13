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
    Authorization authorization1 = new Authorization("code", "id", "123456", Collections.singleton("redirectURI"), "identityId");
    Authorization authorization2 = new Authorization("code", "id", "123456", Collections.singleton("redirectURI"), "identityId");

    assertThat(authorization1, is(authorization2));
  }

  @Test
  public void areNotEqual() {
    Authorization authorization1 = new Authorization("code1", "id1", "123456", Collections.singleton("redirectURI1"), "identityId");
    Authorization authorization2 = new Authorization("code2", "id2", "654321", Collections.singleton("redirectURI2"), "identityId");

    assertThat(authorization1, is(not(authorization2)));
  }
}
