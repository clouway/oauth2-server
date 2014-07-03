package com.example.auth.core;

import com.google.inject.Inject;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class TokenSecurityImpl implements TokenSecurity {
  private final ClientAuthentication clientAuthentication;
  private final AuthorizationVerifier authorizationVerifier;
  private final TokenCreator tokenCreator;

  @Inject
  TokenSecurityImpl(ClientAuthentication clientAuthentication, AuthorizationVerifier authorizationVerifier, TokenCreator tokenCreator) {
    this.clientAuthentication = clientAuthentication;
    this.authorizationVerifier = authorizationVerifier;
    this.tokenCreator = tokenCreator;
  }

  @Override
  public Token create(TokenRequest request) {
    if (!clientAuthentication.authenticate(request.clientId, request.clientSecret)) {
      throw new TokenErrorResponse("invalid_client", "The client is not authenticated!");
    }

    if (!authorizationVerifier.verify(request.code, request.clientId)) {
      throw new TokenErrorResponse("invalid_grant", "The authorization code does not exist!");
    }

    return tokenCreator.create();
  }
}