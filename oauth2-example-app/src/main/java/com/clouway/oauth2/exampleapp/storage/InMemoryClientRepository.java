package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.client.ServiceAccount;
import com.clouway.oauth2.client.ServiceAccountRepository;
import com.clouway.oauth2.jwt.Jwt.ClaimSet;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryClientRepository implements ClientRepository, ServiceAccountRepository {
  private Map<String, Client> clients = Maps.newHashMap();
  private Map<String, ServiceAccount> serviceAccounts = Maps.newHashMap();

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

  @Override
  public Optional<ServiceAccount> findServiceAccount(ClaimSet claimSet) {
    if (serviceAccounts.containsKey(claimSet.iss)) {
      return Optional.of(serviceAccounts.get(claimSet.iss));
    }
    return Optional.absent();
  }

  public void registerServiceAccount(String clientEmail, String privateKeyAsPem) {
    serviceAccounts.put(clientEmail,new ServiceAccount(clientEmail, privateKeyAsPem));
  }
}