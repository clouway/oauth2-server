package com.example.auth.core;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenTest {
  @Test
  public void areEqual() {
    Token token1 = new Token("value", "type");
    Token token2 = new Token("value", "type");

    assertThat(token1, is(token2));
  }

  @Test
  public void areNotEqual() {
    Token token1 = new Token("value1", "type1");
    Token token2 = new Token("value2", "type2");

    assertThat(token1, is(not(token2)));
  }
}