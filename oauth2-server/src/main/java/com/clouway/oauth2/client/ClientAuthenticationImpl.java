package com.clouway.oauth2.client;

import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class ClientAuthenticationImpl implements ClientAuthentication {


  private ClientRepository repository;

  @Inject
  public ClientAuthenticationImpl(ClientRepository repository) {
    this.repository = repository;
  }

  @Override
  public Boolean authenticate(String id, String secret) {
    Optional<Client> client = repository.findById(id);
    if (client.isPresent() && client.get().secret.equals(secret)) {
      return true;
    }

    return false;
  }
}
