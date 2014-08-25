package com.example.auth.core.authorization;

import com.example.auth.core.client.Client;
import com.example.auth.core.client.ClientRepository;
import com.example.auth.core.token.TokenGenerator;
import com.example.auth.core.user.UserIdFinder;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthorizationSecurityImpl implements AuthorizationSecurity {
  private final ClientRepository clientRepository;
  private final ClientAuthorizationRepository clientAuthorizationRepository;
  private final TokenGenerator tokenGenerator;
  private UserIdFinder userIdFinder;

  @Inject
  AuthorizationSecurityImpl(ClientRepository clientRepository,
                            ClientAuthorizationRepository clientAuthorizationRepository,
                            TokenGenerator tokenGenerator,
                            UserIdFinder userIdFinder) {
    this.clientRepository = clientRepository;
    this.clientAuthorizationRepository = clientAuthorizationRepository;
    this.tokenGenerator = tokenGenerator;
    this.userIdFinder = userIdFinder;
  }

  @Override
  public AuthorizationResponse auth(AuthorizationRequest request) {
    Optional<Client> client = clientRepository.findById(request.clientId);

    if (!client.isPresent()) {
      throw new AuthorizationErrorResponse("Invalid App ID: " + request.clientId);
    }

    String code = tokenGenerator.generate();
    String redirectURI = client.get().redirectURI;

    //loading user info by the sessionId
    String  userId =  userIdFinder.find(request.sessionId);

    clientAuthorizationRepository.register(new Authorization(request.responseType, request.clientId, code, redirectURI, userId));

    return new AuthorizationResponse(code, redirectURI);
  }
}