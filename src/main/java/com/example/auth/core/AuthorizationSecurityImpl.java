package com.example.auth.core;

import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class AuthorizationSecurityImpl implements AuthorizationSecurity {
  private final ClientFinder clientFinder;
  private final AuthorizationStore authorizationStore;
  private final TokenGenerator tokenGenerator;

  @Inject
  AuthorizationSecurityImpl(ClientFinder clientFinder, AuthorizationStore authorizationStore, TokenGenerator tokenGenerator) {
    this.clientFinder = clientFinder;
    this.authorizationStore = authorizationStore;
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public AuthorizationResponse auth(AuthorizationRequest request) {
    Optional<Client> client = clientFinder.findById(request.clientId);

    if (!client.isPresent()) {
      throw new AuthorizationErrorResponse("Invalid App ID: " + request.clientId);
    }

    String code = tokenGenerator.generate();
    String redirectURI = client.get().redirectURI;

    authorizationStore.register(new Authorization(request.responseType, request.clientId, code, redirectURI));

    return new AuthorizationResponse(code, redirectURI);
  }
}