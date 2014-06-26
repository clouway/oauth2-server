package com.example.auth.http;

import com.example.auth.core.TokenRepository;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Service
@At("/verify")
public class VerificationEndpoint {
  private final TokenRepository tokenRepository;

  @Inject
  public VerificationEndpoint(TokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  @Get
  @At("/:token")
  public Reply<?> verify(@Named("token") String token) {
    if (!tokenRepository.verify(token)) {
      return Reply.saying().status(SC_BAD_REQUEST);
    }

    return Reply.saying().ok();
  }
}