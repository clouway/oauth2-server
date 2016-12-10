package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;

/**
 * IssueNewTokenActivity is representing the activity which is performed for issuing of new token.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class IssueNewTokenActivity implements ClientActivity {

  private final Tokens tokens;
  private final ClientAuthorizationRepository clientAuthorizationRepository;

  IssueNewTokenActivity(Tokens tokens, ClientAuthorizationRepository clientAuthorizationRepository) {
    this.tokens = tokens;
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

    TokenResponse response = tokens.issueToken(GrantType.AUTHORIZATION_CODE, client, authorization.identityId, instant);
    if (!response.isSuccessful()) {
      return OAuthError.invalidRequest("Token cannot be issued due internal error");
    }

    return new BearerTokenResponse(response.accessToken, response.ttlInSeconds, response.refreshToken);
  }
}
