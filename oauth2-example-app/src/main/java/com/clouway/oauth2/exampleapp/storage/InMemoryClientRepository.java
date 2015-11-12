package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryClientRepository implements ClientRepository {
  private Map<String, Client> clients = Maps.newHashMap();


  @Inject
  public InMemoryClientRepository() {
  }

  @Override
  public void save(Client client) {
    clients.put(client.id, client);
  }

  @Override
  public Optional<Client> findById(String id) {
    return Optional.fromNullable(clients.get(id));
  }

}