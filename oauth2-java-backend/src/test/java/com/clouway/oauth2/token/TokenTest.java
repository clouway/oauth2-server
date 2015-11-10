package com.clouway.oauth2.token;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenTest {
  @Test
  public void areEqual() {
    Date creationDate = new Date();
    Token token1 = new Token("value", "type", "refreshToken", "userId", 1l, creationDate);
    Token token2 = new Token("value", "type", "refreshToken", "userId", 1l, creationDate);

    assertThat(token1, is(token2));
  }

  @Test
  public void areNotEqual() {
    Date creationDate = new Date();
    Token token1 = new Token("value1", "type1", "refreshToken", "userId", 1l, creationDate);
    Token token2 = new Token("value2", "type2", "refreshToken", "userId", 1l, creationDate);

    assertThat(token1, is(not(token2)));
  }

  @Test
  public void areNotEqualWhenDifferentExpirationTimes() {

    System.out.println(new Date().getTime());
    Token token1 = new Token("value", "type", "refreshToken", "userId", 1l, new Date(1408532291030l));
    Token token2 = new Token("value", "type", "refreshToken", "userId", 1l, new Date(1408532291031l));

    assertThat(token1, is(not(token2)));
  }
}