package com.example.auth.core.token;

import com.example.auth.core.authorization.TokenCreationVerifier;
import com.example.auth.core.client.ClientAuthentication;
import com.google.inject.Inject;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenSecurityImpl implements TokenSecurity {
  private final ClientAuthentication clientAuthentication;
  private final TokenCreationVerifier tokenCreationVerifier;

  @Inject
  TokenSecurityImpl(ClientAuthentication clientAuthentication,
                    TokenCreationVerifier tokenCreationVerifier

                    ) {
    this.clientAuthentication = clientAuthentication;
    this.tokenCreationVerifier = tokenCreationVerifier;
  }


  @Override
  public void validateRefreshToken(TokenRequest request) {
    if (!tokenCreationVerifier.verifyRefreshToken(request.clientId, request.clientSecret, request.refreshToken)) {
      throw new TokenErrorResponse("invalid_client", "Invalid token request!");
    }
  }

  @Override
  public void validateAuthCode(TokenRequest request) {
    if (!clientAuthentication.authenticate(request.clientId, request.clientSecret)) {
      throw new TokenErrorResponse("invalid_client", "The client is not authenticated!");
    }
    if (!tokenCreationVerifier.verify(request.code, request.clientId)) {
      throw new TokenErrorResponse("invalid_grant", "The authorization code does not exist!");
    }
  }
}