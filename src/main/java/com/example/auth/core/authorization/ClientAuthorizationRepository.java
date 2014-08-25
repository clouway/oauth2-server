package com.example.auth.core.authorization;

import com.google.common.base.Optional;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ClientAuthorizationRepository {
  void register(Authorization authorization);

  Optional<Authorization> findByCode(String authorizationCode);

  void update(Authorization authorization);
}