package com.clouway.oauth2.authorization;

import com.clouway.oauth2.client.Client;
import com.google.common.base.Optional;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ClientAuthorizationRepository {
  void register(Authorization authorization);

  Optional<Authorization> authorize(Client client, String authCode);

  Optional<Authorization> authorize(Client client, String userId, String responseType);
}