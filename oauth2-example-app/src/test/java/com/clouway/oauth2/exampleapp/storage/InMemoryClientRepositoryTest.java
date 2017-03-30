package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientAlreadyExistsException;
import com.clouway.oauth2.client.ClientRegistrationRequest;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.exampleapp.ClientRepositoryContractTest;
import com.google.common.base.Optional;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryClientRepositoryTest implements ClientRepository {

  private InMemoryClientRepository repository = new InMemoryClientRepository();

  @Override
  public Client register(ClientRegistrationRequest request) throws ClientAlreadyExistsException {
    return repository.register(request);
  }

  @Override
  public Optional<Client> findById(String id) {
    return repository.findById(id);
  }
}