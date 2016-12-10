package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import org.junit.Test;

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
    Token token1 = new Token("value", TokenType.BEARER, GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", creationDate);
    Token token2 = new Token("value", TokenType.BEARER, GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", creationDate);

    assertThat(token1, is(token2));
  }

  @Test
  public void areNotEqual() {
    DateTime creationDate = new DateTime();
    Token token1 = new Token("value1", TokenType.BEARER, GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", creationDate);
    Token token2 = new Token("value2", TokenType.BEARER, GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", creationDate);

    assertThat(token1, is(not(token2)));
  }

  @Test
  public void areNotEqualWhenDifferentExpirationTimes() {
    Token token1 = new Token("value", TokenType.BEARER, GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", new DateTime(new Date(1408532291030L)));
    Token token2 = new Token("value", TokenType.BEARER, GrantType.AUTHORIZATION_CODE, "identityId", "::client id::", new DateTime(new Date(1408532291031L)));

    assertThat(token1, is(not(token2)));
  }
}