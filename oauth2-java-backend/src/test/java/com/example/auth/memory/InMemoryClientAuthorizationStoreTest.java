package com.example.auth.memory;

import com.clouway.oauth2.authorization.ClientAuthorizationRepositoryContractTest;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;

public class InMemoryClientAuthorizationStoreTest extends ClientAuthorizationRepositoryContractTest {

  private InMemoryClientAuthorizationRepository repository = new InMemoryClientAuthorizationRepository();

  @Override
  protected ClientAuthorizationRepository createClientAuthorizationRepository() {
    return repository;
  }
}