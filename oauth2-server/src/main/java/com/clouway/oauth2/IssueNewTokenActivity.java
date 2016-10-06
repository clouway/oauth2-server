package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;

/**
 * IssueNewTokenActivity is representing the activity which is performed for issuing of new token.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class IssueNewTokenActivity implements ClientActivity {

  private final Tokens tokens;
  private final ClientAuthorizationRepository clientAuthorizationRepository;

  public IssueNewTokenActivity(Tokens tokens,
                               ClientAuthorizationRepository clientAuthorizationRepository) {
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

    Token newToken = tokens.issueToken(GrantType.AUTHORIZATION_CODE, client, authorization.identityId, instant);

    return new BearerTokenResponse(newToken.value, newToken.expiresInSeconds, newToken.refreshToken);
  }
}
