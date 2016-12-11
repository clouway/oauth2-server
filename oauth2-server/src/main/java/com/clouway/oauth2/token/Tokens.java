package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.client.Client;
import com.google.common.base.Optional;

import java.util.Set;

/**
 * Tokens is responsible for issuing and retriving of issued tokens.
 *
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface Tokens {

  /**
   * Find token which is not expired till the provided time.
   *
   * @param token then token for which is looked
   * @return an optional token value or absent value if not present
   */
  Optional<BearerToken> findTokenAvailableAt(String token, DateTime when);

  /**
   * Refreshes token using the access token.
   *
   * @param token the access token
   * @param when  token is going to be refreshed
   * @return the refreshed token
   */
  TokenResponse refreshToken(String token, DateTime when);

  /**
   * Ussues a new token for the provided identity.
   *
   * @param grantType  type of the taken to be issued - JWT or Bearer
   * @param client     the client to which token will be ussued
   * @param identityId the identityId for which token was issued
   * @param scopes     requested scopes
   * @param when       the requested time on which it should be issued
   * @return the newly issued token
   */
  TokenResponse issueToken(GrantType grantType, Client client, String identityId, Set<String> scopes, DateTime when);

  /**
   * Revokes token from repository.
   *
   * @param token the token which to be revoked
   */
  void revokeToken(String token);


}