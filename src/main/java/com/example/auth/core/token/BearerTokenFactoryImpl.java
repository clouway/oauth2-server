package com.example.auth.core.token;

import com.example.auth.core.Clock;
import com.example.auth.core.Interval;

import java.util.Date;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class BearerTokenFactoryImpl implements TokenFactory {
  private final TokenGenerator tokenGenerator;
  private final Clock clock;
  private final Interval expirationDuration;

  public BearerTokenFactoryImpl(TokenGenerator tokenGenerator, Clock clock, Interval expirationDuration) {
    this.tokenGenerator = tokenGenerator;
    this.clock = clock;
    this.expirationDuration = expirationDuration;
  }

  @Override
  public Token create() {

    String value = tokenGenerator.generate();

    String type = "bearer";
    Date expirationTime = clock.nowPlus(expirationDuration);

    Token token = new Token(value, type, expirationTime);
    return token;
  }
}
