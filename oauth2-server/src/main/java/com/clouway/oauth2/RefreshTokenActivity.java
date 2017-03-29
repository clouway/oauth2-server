package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class RefreshTokenActivity implements ClientActivity {

  private final Tokens tokens;

  RefreshTokenActivity(Tokens tokens) {
    this.tokens = tokens;
  }

  @Override
  public Response execute(Client client, Request request, DateTime instant) {
    String refreshToken = request.param("refresh_token");

    TokenResponse response = tokens.refreshToken(refreshToken, instant);
    if (!response.isSuccessful()) {
      return OAuthError.invalidGrant();
    }

    BearerToken accessToken = response.accessToken;

    return new BearerTokenResponse(accessToken.value, accessToken.ttlSeconds(instant), response.refreshToken,response.idToken);
  }
}
