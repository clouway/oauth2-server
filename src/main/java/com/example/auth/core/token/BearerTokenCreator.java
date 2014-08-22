package com.example.auth.core.token;

import com.example.auth.app.TokenTimeToLive;
import com.example.auth.core.Clock;
import com.example.auth.core.Duration;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */

public class BearerTokenCreator implements TokenCreator {
  private TokenRepository tokenRepository;
  private final TokenGenerator tokenGenerator;
  private final Clock clock;
  private final Duration expirationDuration;

  @Inject
  public BearerTokenCreator(TokenRepository tokenRepository, TokenGenerator tokenGenerator, Clock clock, @TokenTimeToLive Duration expirationDuration) {
    this.tokenRepository = tokenRepository;
    this.tokenGenerator = tokenGenerator;
    this.clock = clock;
    this.expirationDuration = expirationDuration;
  }

  @Override
  public Token create() {

    String value = tokenGenerator.generate();

    String type = "bearer";

    Token token = new Token(value,  type, expirationDuration.seconds, clock.now());

    tokenRepository.save(token);

    return token;
  }
}
