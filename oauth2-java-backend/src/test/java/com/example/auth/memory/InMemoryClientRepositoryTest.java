package com.example.auth.memory;

import com.example.auth.core.client.ClientRepository;
import com.example.auth.core.client.ClientRepositoryContractTest;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryClientRepositoryTest extends ClientRepositoryContractTest {

  private InMemoryClientRepository repository = new InMemoryClientRepository();

  @Override
  public ClientRepository createRepository() {
    return repository;
  }
}