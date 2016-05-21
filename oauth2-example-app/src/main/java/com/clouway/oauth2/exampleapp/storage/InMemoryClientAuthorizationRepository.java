package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
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
  public void register(Authorization authorization) {
    authorizations.put(authorization.code, authorization);
  }

  @Override
  public Optional<Authorization> authorize(Client client, String authCode) {

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
  public Optional<Authorization> authorize(Client client, String identityId, String responseType) {
    String code = tokenGenerator.generate();
    String redirectURI = client.redirectURI;

    Authorization authorization = new Authorization(responseType, client.id, code, redirectURI, identityId);
    register(authorization);

    return Optional.of(authorization);
  }
}