package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.FindIdentityRequest;
import com.clouway.oauth2.token.Identity;
import com.clouway.oauth2.token.IdentityFinder;
import com.google.common.base.Optional;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public class IdentityAuthorizationActivity implements AuthorizedClientActivity {
  private final IdentityFinder identityFinder;
  private final AuthorizedIdentityActivity authorizedIdentityActivity;

  public IdentityAuthorizationActivity(IdentityFinder identityFinder, AuthorizedIdentityActivity authorizedIdentityActivity) {
    this.identityFinder = identityFinder;
    this.authorizedIdentityActivity = authorizedIdentityActivity;
  }

  @Override
  public Response execute(Authorization authorization, Client client, Request request, DateTime instant) {
    FindIdentityRequest findIdentityRequest = new FindIdentityRequest(authorization.identityId, GrantType.AUTHORIZATION_CODE, instant, authorization.params, authorization.clientId);

    Optional<Identity> possibleIdentity = identityFinder.findIdentity(findIdentityRequest);
    if (!possibleIdentity.isPresent()) {
      return OAuthError.invalidGrant("identity was not found");
    }

    Identity identity = possibleIdentity.get();

    return authorizedIdentityActivity.execute(client, identity, authorization.scopes, request, instant, authorization.params);
  }
}
