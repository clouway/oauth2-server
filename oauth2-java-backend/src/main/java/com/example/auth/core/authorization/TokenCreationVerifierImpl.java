package com.example.auth.core.authorization;

import com.example.auth.core.Clock;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
class TokenCreationVerifierImpl implements TokenCreationVerifier {

  private ClientAuthorizationRepository repository;
  private Clock clock;

  @Inject
  public TokenCreationVerifierImpl(ClientAuthorizationRepository repository, Clock clock) {
    this.repository = repository;
    this.clock = clock;
  }

  @Override
  public Boolean verify(String authCode, String clintId) {
    Optional<Authorization> authorization = repository.findByCode(authCode);

    if (authorization.isPresent()) {

      Authorization authorizationRequest = authorization.get();

      if (authorizationRequest.clientId.equals(clintId) && authorizationRequest.isNotUsed()) {

        authorizationRequest.usedOn(clock.now());
        repository.update(authorizationRequest);

        return true;
      }
    }

    return false;
  }

}
