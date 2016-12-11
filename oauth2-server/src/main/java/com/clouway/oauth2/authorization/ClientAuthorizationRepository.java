package com.clouway.oauth2.authorization;

import com.clouway.oauth2.client.Client;
import com.google.common.base.Optional;

import java.util.Set;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ClientAuthorizationRepository {

  Optional<Authorization> findAuthorization(Client client, String authCode);

  Optional<Authorization> authorize(Client client, String identityId, Set<String> scopes, String responseType);

}