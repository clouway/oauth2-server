package com.clouway.oauth2;

import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenRepository;
import com.google.common.base.Optional;

/**
 * IssueNewTokenActivity is representing the activity which is performed for issuing of new token.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class IssueNewTokenActivity implements ClientActivity {

  private final TokenRepository tokenRepository;
  private final ClientAuthorizationRepository clientAuthorizationRepository;

  public IssueNewTokenActivity(TokenRepository tokenRepository,
                               ClientAuthorizationRepository clientAuthorizationRepository) {
    this.tokenRepository = tokenRepository;
    this.clientAuthorizationRepository = clientAuthorizationRepository;
  }

  @Override
  public Response execute(Client client, Request request) {
    String authCode = request.param("code");

    Optional<Authorization> opt = clientAuthorizationRepository.authorize(client, authCode);

    if (!opt.isPresent()) {
      return OAuthError.invalidGrant();

    }

    Authorization authorization = opt.get();

    Token newToken = tokenRepository.issueToken(authorization.identityId, Optional.<String>absent());

    return new BearerTokenResponse(newToken.value, newToken.expiresInSeconds, newToken.refreshToken);
  }
}
