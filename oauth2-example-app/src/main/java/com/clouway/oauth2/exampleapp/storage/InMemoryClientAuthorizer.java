package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.AuthorizationRequest;
import com.clouway.oauth2.authorization.ClientAuthorizationResult;
import com.clouway.oauth2.authorization.ClientAuthorizer;
import com.clouway.oauth2.authorization.FindAuthorizationResult;
import com.clouway.oauth2.authorization.FindAuthorizationResult.NotFound;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.oauth2.common.DateTime;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryClientAuthorizer implements ClientAuthorizer {
  private final Map<String, Authorization> authorizations = Maps.newHashMap();
  private final ClientFinder clientFinder;

  public InMemoryClientAuthorizer(ClientFinder clientFinder) {
    this.clientFinder = clientFinder;
  }

  @Override
  public FindAuthorizationResult findAuthorization(String clientId, String authCode, DateTime instant) {

    Authorization authorization = authorizations.get(authCode);
    // No authorization was found for that code ?
    if (authorization == null || authorization.wasAlreadyUsed()) {
      return new FindAuthorizationResult.NotFound();
    }

    Optional<Client> possibleClient = clientFinder.findClient(clientId);
    if (!possibleClient.isPresent()) {
      return new FindAuthorizationResult.ClientNotFound();
    }
    
    authorization.usedOn(new Date());

    authorizations.put(authCode, authorization);

    return new FindAuthorizationResult.Success(authorization, possibleClient.get());
  }

  private void register(Authorization authorization) {
    authorizations.put(authorization.code, authorization);
  }

  @NotNull
  @Override
  public ClientAuthorizationResult authorizeClient(@NotNull AuthorizationRequest req) {
    return null;
  }

}