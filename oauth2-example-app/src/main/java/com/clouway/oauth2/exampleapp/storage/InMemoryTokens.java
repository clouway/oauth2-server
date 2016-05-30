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
  private Date currentDate;
  private Duration timeToLive;

  @Inject
  public InMemoryTokens(TokenGenerator tokenGenerator, Date currentDate, Duration timeToLive) {
    this.tokenGenerator = tokenGenerator;
    this.currentDate = currentDate;
    this.timeToLive = timeToLive;
  }

  @Override
  public Optional<Token> getNotExpiredToken(String tokenValue) {
    if (tokens.containsKey(tokenValue)) {
      Token token = tokens.get(tokenValue);

      if (!token.isExpiredOn(currentDate)) {
        //update token expirationDate time
        //remove the current token
        tokens.remove(tokenValue);
        // new instance
        Token updatedToken = new Token(token.value, token.type, token.refreshToken, token.identityId, timeToLive.seconds, currentDate);
        //add the new token
        tokens.put(tokenValue, updatedToken);

        return Optional.of(token);
      }
    }

    return Optional.absent();
  }

  @Override
  public Optional<Token> refreshToken(String refreshToken) {
    for (Token token : tokens.values()) {
      if (refreshToken.equals(token.refreshToken)) {

        tokens.remove(token.value);

        String newTokenValue = tokenGenerator.generate();

        Token updatedToken = new Token(newTokenValue, TokenType.BEARER, token.refreshToken, token.identityId, timeToLive.seconds, currentDate);

        //add the new token
        tokens.put(updatedToken.value, updatedToken);

        return Optional.of(token);
      }
    }
    return Optional.absent();
  }

  @Override
  public Token issueToken(String identityId) {
    String token = tokenGenerator.generate();
    String refreshTokenValue = tokenGenerator.generate();

    Token bearerToken = new Token(token, TokenType.BEARER, refreshTokenValue, identityId, timeToLive.seconds, currentDate);

    tokens.put(token, bearerToken);

    return bearerToken;
  }
}