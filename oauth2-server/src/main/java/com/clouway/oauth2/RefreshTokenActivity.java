package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenRepository;
import com.google.common.base.Optional;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RefreshTokenActivity implements ClientActivity {

  private final TokenRepository tokenRepository;

  public RefreshTokenActivity(TokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  @Override
  public Response execute(Client client, Request request) {
    String refreshToken = request.param("refresh_token");

    Optional<Token> opt = tokenRepository.refreshToken(refreshToken);

    if (!opt.isPresent()) {
      return OAuthError.invalidGrant();
    }

    Token token = opt.get();

    return new BearerTokenResponse(token.value, token.expiresInSeconds, token.refreshToken);
  }
}
