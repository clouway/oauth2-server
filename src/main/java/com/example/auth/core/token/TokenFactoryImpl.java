package com.example.auth.core.token;

import com.example.auth.core.AccessTokenGenerator;
import com.example.auth.core.Clock;
import com.example.auth.core.Interval;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TokenFactoryImpl implements TokenFactory {
  private final AccessTokenGenerator tokenGenerator;
  private final Clock clock;
  private final Interval expirationDuration;

  public TokenFactoryImpl(AccessTokenGenerator tokenGenerator, Clock clock, Interval expirationDuration) {
    this.tokenGenerator = tokenGenerator;
    this.clock = clock;
    this.expirationDuration = expirationDuration;
  }

  @Override
  public Token create() {

    Token token = tokenGenerator.generate();

    token.setExpiration(clock.nowPlus(expirationDuration));

    return token;
  }
}
