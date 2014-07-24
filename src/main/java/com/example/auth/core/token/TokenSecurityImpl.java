package com.example.auth.core.token;

import com.example.auth.core.authorization.ClientAuthorizationRequestVerifier;
import com.example.auth.core.client.ClientAuthentication;
import com.google.inject.Inject;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenSecurityImpl implements TokenSecurity {
  private final ClientAuthentication clientAuthentication;
  private final ClientAuthorizationRequestVerifier clientAuthorizationRequestVerifier;
  private final TokenRepository tokenRepository;
  private TokenFactory tokenFactory;

  @Inject
  TokenSecurityImpl(ClientAuthentication clientAuthentication, ClientAuthorizationRequestVerifier clientAuthorizationRequestVerifier, TokenRepository tokenRepository, TokenFactory tokenFactory) {
    this.clientAuthentication = clientAuthentication;
    this.clientAuthorizationRequestVerifier = clientAuthorizationRequestVerifier;
    this.tokenRepository = tokenRepository;
    this.tokenFactory = tokenFactory;
  }

  @Override
  public Token create(TokenRequest request) {
    if (!clientAuthentication.authenticate(request.clientId, request.clientSecret)) {
      throw new TokenErrorResponse("invalid_client", "The client is not authenticated!");
    }

    if (!clientAuthorizationRequestVerifier.verify(request.code, request.clientId)) {
      throw new TokenErrorResponse("invalid_grant", "The authorization code does not exist!");
    }

    Token token = tokenFactory.create();

    tokenRepository.save(token);

    return token;
  }
}