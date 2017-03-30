package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRegistrationRequest;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.oauth2.client.ClientKeyStore;
import com.clouway.oauth2.exampleapp.ClientRegistry;
import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Pem.Block;
import com.clouway.oauth2.jwt.Jwt.ClaimSet;
import com.clouway.oauth2.jwt.Jwt.Header;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryClientRepository implements ClientRegistry, ClientFinder, ClientKeyStore {
  private final Map<String, Client> clients = Maps.newHashMap();
  private final Map<String, Pem.Block> serviceAccountKeys = Maps.newHashMap();
  private final Pem pem = new Pem();

  @Inject
  public InMemoryClientRepository() {
  }

  @Override
  public Client register(Client client) {
    clients.put(client.id, client);
    return client;
  }

  @Override
  public Client register(ClientRegistrationRequest request) {
    String randomId = UUID.randomUUID().toString();
    Client client =
            new Client(randomId, request.secret, request.description, request.redirectURLs);
    clients.put(client.id, client);
    return client;
  }

  @Override
  public Optional<Client> findClient(String clientId) {
    return Optional.fromNullable(clients.get(clientId));
  }

  @Override
  public Optional<Block> findKey(Header header, ClaimSet claimSet) {
    return null;
  }

  public void registerServiceAccount(String clientEmail, String privateKeyAsPem) {
    try {
      serviceAccountKeys.put(clientEmail, pem.parse(new ByteArrayInputStream(privateKeyAsPem.getBytes())));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}