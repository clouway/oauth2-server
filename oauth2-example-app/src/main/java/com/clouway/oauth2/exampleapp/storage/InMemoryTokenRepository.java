package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.Duration;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenGenerator;
import com.clouway.oauth2.token.TokenRepository;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import java.util.Date;
import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryTokenRepository implements TokenRepository {
  private final Map<String, Token> tokens = Maps.newHashMap();
  private final TokenGenerator tokenGenerator;
  private Date currentDate;
  private Duration timeToLive;

  @Inject
  public InMemoryTokenRepository(TokenGenerator tokenGenerator, Date currentDate, Duration timeToLive) {
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
        Token updatedToken = new Token(token.value, token.type, token.refreshToken, token.userId, timeToLive.seconds, currentDate);
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
        String newRefreshToken = tokenGenerator.generate();
        Token updatedToken = issueToken(token.userId, Optional.of(newRefreshToken));

        //add the new token
        tokens.put(updatedToken.value, updatedToken);

        return Optional.of(token);
      }
    }
    return Optional.absent();
  }

  @Override
  public Token issueToken(String userId, Optional<String> refreshToken) {
    String token = tokenGenerator.generate();
    String refreshTokenValue = "";

    if (refreshToken.isPresent()) {
      refreshTokenValue = refreshToken.get();
    }

    Token bearerToken = new Token(token, "bearer", refreshTokenValue, userId, timeToLive.seconds, currentDate);

    tokens.put(token, bearerToken);

    return bearerToken;
  }
}