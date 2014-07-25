package com.example.auth.memory;

import com.example.auth.core.ClientAuthorizationRequest;
import com.example.auth.core.authorization.ClientAuthorizationRepository;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryClientAuthorizationRepository implements ClientAuthorizationRepository {
  private final Map<String, ClientAuthorizationRequest> authorizations = Maps.newHashMap();

  @Override
  public void register(ClientAuthorizationRequest clientAuthorizationRequest) {
    authorizations.put(clientAuthorizationRequest.code, clientAuthorizationRequest);
  }

  @Override
  public Optional<ClientAuthorizationRequest> findByCode(String authorizationCode) {
    return Optional.fromNullable(authorizations.get(authorizationCode));
  }
}