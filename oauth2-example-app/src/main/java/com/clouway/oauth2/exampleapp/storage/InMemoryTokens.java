package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.Duration;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.TokenGenerator;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryTokens implements Tokens {
  private final Map<String, BearerToken> tokens = Maps.newHashMap();
  private final Map<String, String> refreshTokenToAccessToken = Maps.newHashMap();
  private final TokenGenerator tokenGenerator;
  private Duration timeToLive;

  @Inject
  public InMemoryTokens(TokenGenerator tokenGenerator, Duration timeToLive) {
    this.tokenGenerator = tokenGenerator;
    this.timeToLive = timeToLive;
  }

  @Override
  public Optional<BearerToken> findTokenAvailableAt(String tokenValue, DateTime instant) {
    if (tokens.containsKey(tokenValue)) {
      BearerToken token = tokens.get(tokenValue);

      if (!token.expiresAt(instant)) {
        //update token expirationDate time
        //remove the current token
        tokens.remove(tokenValue);
        // new instance
        BearerToken updatedToken = new BearerToken(token.value, token.grantType, token.identityId, token.clientId, Collections.<String>emptySet(), instant);
        //add the new token
        tokens.put(tokenValue, updatedToken);

        return Optional.of(token);
      }
    }

    return Optional.absent();
  }

  @Override
  public TokenResponse refreshToken(String refreshToken, DateTime instant) {
    if (refreshTokenToAccessToken.containsKey(refreshToken)) {
      String accessToken = refreshTokenToAccessToken.get(refreshToken);
      BearerToken oldToken = tokens.get(accessToken);
      tokens.remove(accessToken);

      String newTokenValue = tokenGenerator.generate();
      BearerToken updatedToken = new BearerToken(newTokenValue, oldToken.grantType, oldToken.identityId, oldToken.clientId, Collections.<String>emptySet(), instant);

      tokens.put(newTokenValue, updatedToken);
      refreshTokenToAccessToken.put(refreshToken, newTokenValue);

      return new TokenResponse(true, updatedToken.value, refreshToken, updatedToken.ttlSeconds(instant));

    }

    return new TokenResponse(false, "", "", 0L);
  }

  @Override
  public TokenResponse issueToken(GrantType grantType, Client client, String identityId, Set<String> scopes, DateTime when) {
    String token = tokenGenerator.generate();
    String refreshTokenValue = tokenGenerator.generate();

    BearerToken bearerToken = new BearerToken(token, GrantType.JWT, identityId, client.id, scopes, when);
    tokens.put(token, bearerToken);

    return new TokenResponse(true, token, refreshTokenValue, bearerToken.ttlSeconds(when));
  }

  @Override
  public void revokeToken(String token) {
    tokens.remove(token);
  }
}