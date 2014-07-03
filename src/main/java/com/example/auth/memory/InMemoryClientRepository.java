package com.example.auth.memory;

import com.example.auth.core.Client;
import com.example.auth.core.ClientAuthentication;
import com.example.auth.core.ClientFinder;
import com.example.auth.core.ClientRegister;
import com.example.auth.core.RegistrationRequest;
import com.example.auth.core.RegistrationResponse;
import com.example.auth.core.TokenGenerator;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryClientRepository implements ClientRegister, ClientFinder, ClientAuthentication {
  private Map<String, Client> clients = Maps.newHashMap();

  private TokenGenerator tokenGenerator;

  @Inject
  public InMemoryClientRepository(TokenGenerator tokenGenerator) {
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public RegistrationResponse register(RegistrationRequest request) {
    String id = tokenGenerator.generate();
    String secret = tokenGenerator.generate();

    clients.put(id, new Client(id, secret, request.name, request.url, request.description, request.redirectURI));

    return new RegistrationResponse(id, secret);
  }

  @Override
  public Optional<Client> findById(String id) {
    return Optional.fromNullable(clients.get(id));
  }

  @Override
  public Boolean authenticate(String id, String secret) {
    if (clients.containsKey(id)) {
      if (clients.get(id).secret.equals(secret)) {
        return true;
      }
    }

    return false;
  }
}