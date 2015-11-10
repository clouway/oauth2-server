package com.example.auth.memory;

import com.example.auth.core.authorization.ClientAuthorizationRepositoryContractTest;
import com.example.auth.core.authorization.ClientAuthorizationRepository;

public class InMemoryClientAuthorizationStoreTest extends ClientAuthorizationRepositoryContractTest {

  private InMemoryClientAuthorizationRepository repository = new InMemoryClientAuthorizationRepository();

  @Override
  protected ClientAuthorizationRepository createClientAuthorizationRepository() {
    return repository;
  }
}