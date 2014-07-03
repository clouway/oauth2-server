package com.example.auth.memory;

import com.example.auth.core.Authorization;
import com.example.auth.core.AuthorizationVerifier;
import com.example.auth.core.AuthorizationStore;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryAuthorizationRepository implements AuthorizationStore, AuthorizationVerifier {
  private final Map<String, Authorization> authorizations = Maps.newHashMap();

  @Override
  public void register(Authorization authorization) {
    authorizations.put(authorization.code, authorization);
  }


  @Override
  public Boolean verify(String code, String clintId) {
    if (authorizations.containsKey(code)) {
      if (clintId.equals(authorizations.get(code).clientId)) {
        return true;
      }
    }

    return false;
  }
}