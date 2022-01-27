package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.IdTokenFactory;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.token.FindIdentityRequest;
import com.clouway.oauth2.token.Identity;
import com.clouway.oauth2.token.IdentityFinder;
import com.google.common.base.Optional;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class RefreshTokenActivity implements ClientActivity {

  private final Tokens tokens;
  private final IdTokenFactory idTokenFactory;
  private final IdentityFinder identityFinder;

  RefreshTokenActivity(Tokens tokens, IdTokenFactory idTokenFactory, IdentityFinder identityFinder) {
    this.tokens = tokens;
    this.idTokenFactory = idTokenFactory;
    this.identityFinder = identityFinder;
  }

  @Override
  public Response execute(Client client, Request request, DateTime instant) {
    String refreshToken = request.param("refresh_token");

    TokenResponse response = tokens.refreshToken(refreshToken, instant);
    if (!response.isSuccessful()) {
      return OAuthError.invalidGrant("Provided refresh_token was not found.");
    }
    BearerToken accessToken = response.accessToken;

    Optional<Identity> possibleIdentity = identityFinder.findIdentity(
            new FindIdentityRequest(accessToken.identityId, accessToken.grantType, instant, accessToken.params, accessToken.clientId));

    if (!possibleIdentity.isPresent()) {
      return OAuthError.invalidGrant("identity was not found");
    }
    Optional<String> possibleIdToken = idTokenFactory.create(
            request.header("Host"),
            client.id,
            possibleIdentity.get(),
            response.accessToken.ttlSeconds(instant),
            instant
    );
    if (possibleIdToken.isPresent()) {
      return new BearerTokenResponse(accessToken.value, accessToken.ttlSeconds(instant), response.accessToken.scopes, response.refreshToken, possibleIdToken.get());
    }
    return new BearerTokenResponse(accessToken.value, accessToken.ttlSeconds(instant), response.accessToken.scopes, response.refreshToken);
  }
}
