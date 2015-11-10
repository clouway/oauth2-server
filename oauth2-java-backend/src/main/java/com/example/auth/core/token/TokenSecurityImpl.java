package com.example.auth.core.token;

import com.example.auth.core.authorization.TokenCreationVerifier;
import com.example.auth.core.client.ClientAuthentication;
import com.google.inject.Inject;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class TokenSecurityImpl implements TokenSecurity {
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
  public void validate(ProvidedAuthorizationCode authCode) {

    authenticateClient(authCode.clientId, authCode.clientSecret);

    if (!tokenCreationVerifier.verify(authCode.value, authCode.clientId)) {
      throw new TokenErrorResponse("invalid_grant", "The authorization code does not exist!");
    }
  }

  @Override
  public void authenticateClient(String clientId, String clientSecret) {
    if (!clientAuthentication.authenticate(clientId, clientSecret)) {
      throw new TokenErrorResponse("invalid_client", "The client is not authenticated!");
    }
  }
}