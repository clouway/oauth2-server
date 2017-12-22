package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.google.common.base.Optional;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public class AuthCodeAuthorization implements ClientActivity {
  private final ClientAuthorizationRepository clientAuthorizationRepository;
  private final AuthorizedClientActivity clientActivity;

  public AuthCodeAuthorization(ClientAuthorizationRepository clientAuthorizationRepository, AuthorizedClientActivity clientActivity) {
    this.clientAuthorizationRepository = clientAuthorizationRepository;
    this.clientActivity = clientActivity;
  }

  @Override
  public Response execute(Client client, Request request, DateTime instant) {
    String authCode = request.param("code");

    Optional<Authorization> possibleAuthorization = clientAuthorizationRepository.findAuthorization(client, authCode, instant);

    if (!possibleAuthorization.isPresent()) {
      return OAuthError.invalidGrant("Authorization was not found.");
    }

    Authorization authorization = possibleAuthorization.get();

    return clientActivity.execute(authorization, client, request, instant);
  }
}
