package com.example.auth.core.authorization;

import com.example.auth.core.ClientAuthorizationRequest;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class ClientAuthorizationRequestVerifierImpl implements ClientAuthorizationRequestVerifier {

  private ClientAuthorizationRepository repository;

  @Inject
  public ClientAuthorizationRequestVerifierImpl(ClientAuthorizationRepository repository) {

    this.repository = repository;
  }

  @Override
  public Boolean verify(String code, String clintId) {
    Optional<ClientAuthorizationRequest> result = repository.findByCode(code);

    if (result.isPresent()) {
      if (result.get().clientId.equals(clintId)) {
        return true;
      }
    }

    return false;
  }
}
