package com.clouway.oauth2;

import com.clouway.oauth2.authorization.AuthorizationResponse;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthorizationResponseTest {
  @Test
  public void buildRedirectURI() throws Exception {
    AuthorizationResponse response = new AuthorizationResponse("123456789", "http://abv.bg/auth");

    assertThat(response.buildURI(), is("http://abv.bg/auth?code=123456789"));
  }

  @Test
  public void buildAnotherRedirectURI() throws Exception {
    AuthorizationResponse response = new AuthorizationResponse("987654321", "http://zazz.bg/");

    assertThat(response.buildURI(), is("http://zazz.bg/?code=987654321"));
  }

  @Test
  public void codeIsUrlSafelyEncoded() throws Exception {
    AuthorizationResponse response = new AuthorizationResponse(
            "a test 23",
            "http://zazz.bg/"
    );

    assertThat(response.buildURI(), is("http://zazz.bg/?code=a+test+23"));
  }
}