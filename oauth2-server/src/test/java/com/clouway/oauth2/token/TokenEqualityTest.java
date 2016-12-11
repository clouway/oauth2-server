package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenEqualityTest {

  @Test
  public void areEqual() {
    DateTime creationDate = new DateTime();
    BearerToken token1 = new BearerToken("value", GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", Collections.<String>emptySet(), creationDate);
    BearerToken token2 = new BearerToken("value", GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", Collections.<String>emptySet(), creationDate);

    assertThat(token1, is(token2));
  }

  @Test
  public void areNotEqual() {
    DateTime creationDate = new DateTime();
    BearerToken token1 = new BearerToken("value1", GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", Collections.<String>emptySet(), creationDate);
    BearerToken token2 = new BearerToken("value2", GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", Collections.<String>emptySet(), creationDate);

    assertThat(token1, is(not(token2)));
  }

  @Test
  public void areNotEqualWhenDifferentExpirationTimes() {
    BearerToken token1 = new BearerToken("value", GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", Collections.<String>emptySet(), new DateTime(new Date(1408532291030L)));
    BearerToken token2 = new BearerToken("value", GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", Collections.<String>emptySet(), new DateTime(new Date(1408532291031L)));

    assertThat(token1, is(not(token2)));
  }
}