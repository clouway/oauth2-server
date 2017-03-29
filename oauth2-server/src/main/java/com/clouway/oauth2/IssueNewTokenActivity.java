package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.IdentityFinder;
import com.google.common.base.Optional;

/**
 * IssueNewTokenActivity is representing the activity which is performed for issuing of new token.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class IssueNewTokenActivity implements ClientActivity {

  private final Tokens tokens;
  private final IdentityFinder identityFinder;
  private final ClientAuthorizationRepository clientAuthorizationRepository;

  IssueNewTokenActivity(Tokens tokens, IdentityFinder identityFinder, ClientAuthorizationRepository clientAuthorizationRepository) {
    this.tokens = tokens;
    this.identityFinder = identityFinder;
    this.clientAuthorizationRepository = clientAuthorizationRepository;
  }

  @Override
  public Response execute(Client client, Request request, DateTime instant) {
    String authCode = request.param("code");

    Optional<Authorization> possibleResponse = clientAuthorizationRepository.findAuthorization(client, authCode);

    if (!possibleResponse.isPresent()) {
      return OAuthError.invalidGrant();
    }

    Authorization authorization = possibleResponse.get();

    Optional<Identity> possibleIdentity = identityFinder.findIdentity(authorization.identityId, GrantType.AUTHORIZATION_CODE, instant);
    if (!possibleIdentity.isPresent()) {
      return OAuthError.invalidGrant();
    }

    Identity identity = possibleIdentity.get();

    TokenResponse response = tokens.issueToken(GrantType.AUTHORIZATION_CODE, client, identity, authorization.scopes, instant);
    if (!response.isSuccessful()) {
      return OAuthError.invalidRequest("Token cannot be issued.");
    }

    BearerToken accessToken = response.accessToken;

    return new BearerTokenResponse(accessToken.value, accessToken.ttlSeconds(instant), response.refreshToken);
  }
}
