package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import com.google.common.base.Optional;

import java.util.Date;

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
  Optional<Token> getNotExpiredToken(String token, DateTime when);

  /**
   * Refreshes token using the access token.
   *
   * @param token the access token
   * @param when token is going to be refreshed
   * @return the refreshed token
   */
  Optional<Token> refreshToken(String token, DateTime when);

  /**
   * Ussues a new token for the provided identity.
   * @param identityId the identityId for which token was issued
   * @param when the requested time on which it should be issued
   * @return
   */
  Token issueToken(String identityId, DateTime when);
}