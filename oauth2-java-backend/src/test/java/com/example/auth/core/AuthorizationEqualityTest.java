package com.example.auth.core;

import com.example.auth.core.authorization.Authorization;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class AuthorizationEqualityTest {
  @Test
  public void areEqual() {
    Authorization authorization1 = new Authorization("code", "id", "123456", "redirectURI", "userId");
    Authorization authorization2 = new Authorization("code", "id", "123456", "redirectURI", "userId");

    assertThat(authorization1, is(authorization2));
  }

  @Test
  public void areNotEqual() {
    Authorization authorization1 = new Authorization("code1", "id1", "123456", "redirectURI1", "userId");
    Authorization authorization2 = new Authorization("code2", "id2", "654321", "redirectURI2", "userId");

    assertThat(authorization1, is(not(authorization2)));
  }
}
