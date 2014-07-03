package com.example.auth.http;

import com.example.auth.core.TokenErrorResponse;
import com.example.auth.core.Token;
import com.example.auth.core.TokenSecurity;
import com.example.auth.core.TokenRequest;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Post;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Service
@At("/token")
public class TokenEndpoint {
  private final TokenSecurity tokenSecurity;

  @Inject
  public TokenEndpoint(TokenSecurity tokenSecurity) {
    this.tokenSecurity = tokenSecurity;
  }

  @Post
  public Reply<?> generate(Request request) {
    try {
      TokenRequest tokenRequest = read(request);

      Token token = tokenSecurity.create(tokenRequest);

      return Reply.with(adapt(token)).as(Json.class).ok();
    } catch (TokenErrorResponse exception) {
      return Reply.with(adapt(exception)).status(SC_BAD_REQUEST).as(Json.class);
    }
  }

  private TokenRequest read(Request request) {
    String grantType = request.param("grant_type");
    String code = request.param("code");
    String clientId = request.param("client_id");
    String clientSecret = request.param("client_secret");

    return new TokenRequest(grantType, code, clientId, clientSecret);
  }

  private TokenDTO adapt(Token token) {
    return new TokenDTO(token.value, token.type);
  }

  private ErrorResponseDTO adapt(TokenErrorResponse exception) {
    return new ErrorResponseDTO(exception.code, exception.description);
  }
}