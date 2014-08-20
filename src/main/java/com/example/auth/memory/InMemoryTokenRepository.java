package com.example.auth.memory;

import com.example.auth.core.token.Token;
import com.example.auth.core.token.TokenRepository;
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
  private Date currentDate;

  @Inject
  public InMemoryTokenRepository(Date currentDate) {
    this.currentDate = currentDate;
  }

  @Override
  public void save(Token token) {
    tokens.put(token.value,token);

  }

  @Override
  public Optional<Token> getNotExpiredToken(String tokenValue) {
    if (tokens.containsKey(tokenValue)) {
          Token token = tokens.get(tokenValue);

          if (currentDate.before(token.expiration)) {
            //update token expiration time
            //remove the current token
            tokens.remove(tokenValue);
            // new instance
            token = token.expiresOn(new Date(token.expiration.getTime() + 900000000));
            //add the new token
            tokens.put(tokenValue, token);

            return Optional.of(token);
          }
        }

    return Optional.absent();
  }
}