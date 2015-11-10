package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.token.TokenVerifier;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Service
public class VerificationEndpoint {
  private final TokenVerifier verifier;

  @Inject
  public VerificationEndpoint(TokenVerifier verifier) {
    this.verifier = verifier;
  }

  @Get
  public Reply<?> verify(@Named("token") String token) {
    if (!verifier.verify(token)) {
      return Reply.saying().status(SC_BAD_REQUEST);
    }

    return Reply.saying().ok();
  }
}