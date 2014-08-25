package com.example.auth.core.authorization;

import com.example.auth.core.token.TokenGenerator;
import com.example.auth.core.client.Client;
import com.example.auth.core.client.ClientRepository;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthorizationSecurityImpl implements AuthorizationSecurity {
  private final ClientRepository clientRepository;
  private final ClientAuthorizationRepository clientAuthorizationRepository;
  private final TokenGenerator tokenGenerator;

  @Inject
  AuthorizationSecurityImpl(ClientRepository clientRepository, ClientAuthorizationRepository clientAuthorizationRepository, TokenGenerator tokenGenerator) {
    this.clientRepository = clientRepository;
    this.clientAuthorizationRepository = clientAuthorizationRepository;
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public AuthorizationResponse auth(AuthorizationRequest request) {
    Optional<Client> client = clientRepository.findById(request.clientId);

    if (!client.isPresent()) {
      throw new AuthorizationErrorResponse("Invalid App ID: " + request.clientId);
    }

    String code = tokenGenerator.generate();
    String redirectURI = client.get().redirectURI;

    clientAuthorizationRepository.register(new ClientAuthorizationRequest(request.responseType, request.clientId, code, redirectURI));

    return new AuthorizationResponse(code, redirectURI);
  }
}