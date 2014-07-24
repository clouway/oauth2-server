package com.example.auth.core.authorization;

import com.example.auth.core.ClientAuthorizationRequest;
import com.google.common.base.Optional;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ClientAuthorizationRepository {
  void register(ClientAuthorizationRequest clientAuthorizationRequest);

  Optional<ClientAuthorizationRequest> findByCode(String authorizationCode);
}