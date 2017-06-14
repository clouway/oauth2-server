package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.IdTokenBuilder;
import com.clouway.oauth2.token.TokenBuilder;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;

import java.util.Set;


/**
 * IssueNewTokenActivity is representing the activity which is performed for issuing of new token.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class IssueNewTokenActivity implements AuthorizedClientActivity {
  private final Tokens tokens;
  private final TokenBuilder idTokenBuilder;

  IssueNewTokenActivity(Tokens tokens, TokenBuilder idTokenBuilder) {
    this.tokens = tokens;
    this.idTokenBuilder = idTokenBuilder;
  }

  @Override
  public Response execute(Client client, Identity identity, Set<String> scopes, Request request, DateTime instant) {
    TokenResponse response = tokens.issueToken(GrantType.AUTHORIZATION_CODE, client, identity, scopes, instant);
    if (!response.isSuccessful()) {
      return OAuthError.invalidRequest("Token cannot be issued.");
    }

    BearerToken accessToken = response.accessToken;
    String idToken = idTokenBuilder.issueToken(request.header("Host"),client.id,identity,accessToken.ttlSeconds(instant),instant);

    return new BearerTokenResponse(accessToken.value, accessToken.ttlSeconds(instant), accessToken.scopes, response.refreshToken, idToken);
  }
}
