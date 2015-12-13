package com.clouway.oauth2.token;

import com.clouway.oauth2.Duration;
import com.google.common.base.Optional;

/**
 * TokenRepository is a repository for all generated security tokens.
 *
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface TokenRepository {

  /**
   * Register is registering a new token in the repository.
   *
   * @param token the token to be registered in the repository.
   */
  void register(Token token);

  /**
   * Find token which is not expired till the provided time.
   *
   * @param token then token for which is looked
   * @return an optional token value or absent value if not present
   */
  Optional<Token> getNotExpiredToken(String token);

  /**
   * Finds token that is associated by the provided refresh token.
   *
   * @param value the value of the refresh token.
   * @return the found token or absent value if token is not found
   */
  Optional<Token> findByRefreshTokenCode(String value);

  /**
   * Refreshes token using the access token.
   *
   * @param token   the access token
   * @return the refreshed token
   */
  Optional<Token> refreshToken(String token);

  /**
   * Issues a new token for the provided user.
   *
   * @param userId       the user of which token need to be issued
   * @param refreshToken the refresh token if there is such one  @return the newly issued token
   */
  Token issueToken(String userId, Optional<String> refreshToken);
}