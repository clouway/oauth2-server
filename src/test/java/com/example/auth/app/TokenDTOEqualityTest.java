package com.example.auth.app;

import com.example.auth.app.TokenDTO;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenDTOEqualityTest {
  @Test
  public void areEqual() {
    TokenDTO token1 = new TokenDTO("value", "type");
    TokenDTO token2 = new TokenDTO("value", "type");

    assertThat(token1, is(token2));
  }

  @Test
  public void areNotEqual() {
    TokenDTO token1 = new TokenDTO("value1", "type1");
    TokenDTO token2 = new TokenDTO("value2", "type2");

    assertThat(token1, is(not(token2)));
  }
}