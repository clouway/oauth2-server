package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryClientAuthorizationRepository implements ClientAuthorizationRepository {
  private final Map<String, Authorization> authorizations = Maps.newHashMap();

  @Override
  public void register(Authorization authorization) {
    authorizations.put(authorization.code, authorization);
  }

  @Override
  public Optional<Authorization> findByCode(String authorizationCode) {
    return Optional.fromNullable(authorizations.get(authorizationCode));
  }

  @Override
  public void update(Authorization authorization) {
    authorizations.remove(authorization.code);
    authorizations.put(authorization.code, authorization);
  }
}