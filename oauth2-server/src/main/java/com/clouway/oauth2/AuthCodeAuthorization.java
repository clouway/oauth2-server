package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.user.IdentityFinder;
import com.clouway.oauth2.util.Params;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class AuthCodeAuthorization implements ClientActivity {

  private final ClientAuthorizationRepository clientAuthorizationRepository;
  private final IdentityFinder identityFinder;
  private final AuthorizedClientActivity authorizedClientActivity;

  AuthCodeAuthorization(ClientAuthorizationRepository clientAuthorizationRepository, IdentityFinder identityFinder, AuthorizedClientActivity authorizedClientActivity) {
    this.clientAuthorizationRepository = clientAuthorizationRepository;
    this.identityFinder = identityFinder;
    this.authorizedClientActivity = authorizedClientActivity;
  }

  @Override
  public Response execute(Client client, Request request, DateTime instant) {
    String authCode = request.param("code");

    Optional<Authorization> possibleAuthorization = clientAuthorizationRepository.findAuthorization(client, authCode, instant);

    if (!possibleAuthorization.isPresent()) {
      return OAuthError.invalidGrant();
    }

    Authorization authorization = possibleAuthorization.get();

    Map<String, String> params  = new Params().parse(request, "code");

    Optional<Identity> possibleIdentity = identityFinder.findIdentity(authorization.identityId, GrantType.AUTHORIZATION_CODE, instant, params);
    if (!possibleIdentity.isPresent()) {
      return OAuthError.invalidGrant("identity was not found");
    }

    Identity identity = possibleIdentity.get();
    return authorizedClientActivity.execute(client, identity, authorization.scopes, request, instant, params);
  }
}
