package com.example.auth.core;

import com.google.inject.Inject;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class TokenSecurityImpl implements TokenSecurity {
  private ResourceOwnerStore resourceOwners;
  private TokenRepository tokens;

  @Inject
  public TokenSecurityImpl(ResourceOwnerStore resourceOwners, TokenRepository tokens) {
    this.resourceOwners = resourceOwners;
    this.tokens = tokens;
  }

  @Override
  public Token create(TokenRequest request) {
/*    Boolean ownerExist = resourceOwners.exist(request.username, request.password);

    if (!ownerExist) {
      throw new ErrorResponseException("invalid_grant", "The username or password is incorrect!");
    }

    return tokens.create();*/

    return null;
  }
}