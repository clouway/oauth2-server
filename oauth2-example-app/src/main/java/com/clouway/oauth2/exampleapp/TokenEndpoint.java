package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.Duration;
import com.clouway.oauth2.token.ProvidedAuthorizationCode;
import com.clouway.oauth2.token.ProvidedRefreshToken;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenCreator;
import com.clouway.oauth2.token.TokenErrorResponse;
import com.clouway.oauth2.token.TokenRequest;
import com.google.inject.Inject;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Post;

import static com.google.common.io.BaseEncoding.base64;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Service
public class TokenEndpoint {
  private static final Duration expirationDuration = Duration.minutes(5);
  private final TokenCreator tokenCreator;

  @Inject
  public TokenEndpoint(
          TokenCreator tokenCreator
  ) {
    this.tokenCreator = tokenCreator;
  }

  @Post
  public Reply<?> generate(Request request) {
    try {
      TokenRequest tokenRequest = read(request);
      Token token;

      if ("refresh_token".equals(tokenRequest.grantType)) {

        token = tokenCreator.create(new ProvidedRefreshToken(tokenRequest.refreshToken, tokenRequest.clientId, tokenRequest.clientSecret), expirationDuration);
      } else {

        token = tokenCreator.create(new ProvidedAuthorizationCode(tokenRequest.code, tokenRequest.clientId, tokenRequest.clientSecret), expirationDuration);
      }

      return Reply.with(adapt(token)).as(Json.class).ok();
    } catch (TokenErrorResponse exception) {
      return Reply.with(adapt(exception)).status(SC_BAD_REQUEST).as(Json.class);
    }
  }

  private TokenRequest read(Request request) {
    String[] credentials = decodeCredentials(request).split(":");

    String clientId = credentials[0];
    String clientSecret = credentials[1];

    String code = request.param("code");
    String grantType = request.param("grant_type");
    String refreshToken = request.param("refresh_token");

    return new TokenRequest(grantType, code, refreshToken, clientId, clientSecret);
  }

  public String decodeCredentials(Request request) {
    String authHeader = request.header("Authorization");

    String credentials = authHeader.substring(6);

    return new String(base64().decode(credentials));
  }


  private TokenDTO adapt(Token token) {
    return new TokenDTO(token.value, token.refreshToken, token.type, token.expiresInSeconds);
  }

  private ErrorResponseDTO adapt(TokenErrorResponse exception) {
    return new ErrorResponseDTO(exception.code, exception.description);
  }
}