package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.authorization.AuthorizationRequest;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.token.TokenGenerator;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Date;
import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryClientAuthorizationRepository implements ClientAuthorizationRepository {
  private final Map<String, Authorization> authorizations = Maps.newHashMap();
  private final TokenGenerator tokenGenerator;

  public InMemoryClientAuthorizationRepository(TokenGenerator tokenGenerator) {
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public Optional<Authorization> findAuthorization(Client client, String authCode, DateTime instant) {

    Authorization authorization = authorizations.get(authCode);
    // No authorization was found for that code ?
    if (authorization == null) {
      return Optional.absent();
    }

    // Authorization was issued for different client is redirect uri ?
    if (!authorization.clientId.equals(client.id) || authorization.wasAlreadyUsed()) {
      return Optional.absent();
    }

    authorization.usedOn(new Date());

    authorizations.put(authCode, authorization);

    return Optional.of(authorization);
  }

  @Override
  public Optional<Authorization> authorize(AuthorizationRequest authorizationRequest) {
    String code = tokenGenerator.generate();

    Authorization authorization = new Authorization(
            authorizationRequest.responseType,
            authorizationRequest.client.id,
            authorizationRequest.identityId,
            code,
            authorizationRequest.scopes,
            authorizationRequest.client.redirectURLs,
            authorizationRequest.codeChallenge,
            authorizationRequest.params);
    register(authorization);

    return Optional.of(authorization);
  }

  private void register(Authorization authorization) {
    authorizations.put(authorization.code, authorization);
  }
}