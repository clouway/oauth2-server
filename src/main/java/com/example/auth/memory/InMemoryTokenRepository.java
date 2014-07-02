package com.example.auth.memory;

import com.example.auth.core.AccessTokenGenerator;
import com.example.auth.core.Clock;
import com.example.auth.core.Interval;
import com.example.auth.core.Token;
import com.example.auth.core.TokenRepository;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryTokenRepository implements TokenRepository {
  private final Map<String, TokenEntity> tokens = Maps.newHashMap();

  private final AccessTokenGenerator tokenGenerator;
  private final Clock clock;
  private final Interval expirationDuration;

  @Inject
  InMemoryTokenRepository(AccessTokenGenerator tokenGenerator, Clock clock, Interval expirationDuration) {
    this.tokenGenerator = tokenGenerator;
    this.clock = clock;
    this.expirationDuration = expirationDuration;
  }

  @Override
  public Token create() {
    Token token = tokenGenerator.generate();

    tokens.put(token.value, new TokenEntity(token.value, token.type, clock.nowPlus(expirationDuration)));

    return token;
  }

  @Override
  public Boolean verify(String token) {
    if (tokens.containsKey(token)) {
      TokenEntity tokenEntity = tokens.get(token);

      if (clock.now().before(tokenEntity.expiration)) {
        tokens.put(token, new TokenEntity(tokenEntity.value, tokenEntity.type, clock.nowPlus(expirationDuration)));

        return true;
      }
    }

    return false;
  }
}