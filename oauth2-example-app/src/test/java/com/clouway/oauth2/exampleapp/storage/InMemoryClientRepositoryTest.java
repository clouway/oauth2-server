package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.oauth2.client.ClientRegistrationRequest;
import com.clouway.oauth2.exampleapp.ClientRegistry;
import com.google.common.base.Optional;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryClientRepositoryTest implements ClientFinder, ClientRegistry {

  private InMemoryClientRepository repository = new InMemoryClientRepository();

  @Override
  public Client register(Client client) {
    return repository.register(client);
  }

  @Override
  public Client register(ClientRegistrationRequest request) {
    return repository.register(request);
  }

  @Override
  public Optional<Client> findClient(String clientId) {
    return repository.findClient(clientId);
  }
}