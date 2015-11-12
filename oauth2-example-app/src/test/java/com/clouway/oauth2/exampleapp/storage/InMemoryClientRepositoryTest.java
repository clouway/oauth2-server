package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.exampleapp.ClientRepositoryContractTest;

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