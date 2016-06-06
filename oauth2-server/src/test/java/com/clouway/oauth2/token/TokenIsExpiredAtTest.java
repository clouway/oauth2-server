package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import org.junit.Test;

import static com.clouway.oauth2.util.CalendarUtil.newTime;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class TokenIsExpiredAtTest {

  @Test
  public void isStillActive() {
    Token token = new Token("", TokenType.BEARER, "", "", 60L, new DateTime(newTime(10, 0, 0)));
    assertThat(token.expiresAt(new DateTime(newTime(10, 0, 0))), is(false));
  }

  @Test
  public void wasExpired() {
    Token token = new Token("", TokenType.BEARER, "", "", 60L, new DateTime(newTime(10, 0, 0)));
    assertThat(token.expiresAt(new DateTime(newTime(10, 1, 1))), is(true));
  }

}