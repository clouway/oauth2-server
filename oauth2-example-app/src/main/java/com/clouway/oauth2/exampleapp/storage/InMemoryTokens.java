package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.Duration;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenGenerator;
import com.clouway.oauth2.token.TokenType;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import java.util.Date;
import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryTokens implements Tokens {
  private final Map<String, Token> tokens = Maps.newHashMap();
  private final TokenGenerator tokenGenerator;
  private Duration timeToLive;

  @Inject
  public InMemoryTokens(TokenGenerator tokenGenerator, Duration timeToLive) {
    this.tokenGenerator = tokenGenerator;
    this.timeToLive = timeToLive;
  }

  @Override
  public Optional<Token> getNotExpiredToken(String tokenValue, Date instant) {
    if (tokens.containsKey(tokenValue)) {
      Token token = tokens.get(tokenValue);

      if (!token.isExpiredOn(instant)) {
        //update token expirationDate time
        //remove the current token
        tokens.remove(tokenValue);
        // new instance
        Token updatedToken = new Token(token.value, token.type, token.refreshToken, token.identityId, timeToLive.seconds, instant);
        //add the new token
        tokens.put(tokenValue, updatedToken);

        return Optional.of(token);
      }
    }

    return Optional.absent();
  }

  @Override
  public Optional<Token> refreshToken(String refreshToken, Date instant) {
    for (Token token : tokens.values()) {
      if (refreshToken.equals(token.refreshToken)) {

        tokens.remove(token.value);

        String newTokenValue = tokenGenerator.generate();

        Token updatedToken = new Token(newTokenValue, TokenType.BEARER, token.refreshToken, token.identityId, timeToLive.seconds, instant);

        //add the new token
        tokens.put(updatedToken.value, updatedToken);

        return Optional.of(token);
      }
    }
    return Optional.absent();
  }

  @Override
  public Token issueToken(String identityId, Date instant) {
    String token = tokenGenerator.generate();
    String refreshTokenValue = tokenGenerator.generate();

    Token bearerToken = new Token(token, TokenType.BEARER, refreshTokenValue, identityId, timeToLive.seconds, instant);

    tokens.put(token, bearerToken);

    return bearerToken;
  }
}