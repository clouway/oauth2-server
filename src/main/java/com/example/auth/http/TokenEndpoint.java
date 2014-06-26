package com.example.auth.http;

import com.example.auth.core.ErrorResponseException;
import com.example.auth.core.Token;
import com.example.auth.core.TokenSecurity;
import com.example.auth.core.TokenRequest;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

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

  @Get
  public Reply<?> generate(Request request) {
    try {
      TokenRequest tokenRequest = adapt(read(request));

      Token token = tokenSecurity.create(tokenRequest);

      return Reply.with(new TokenDTO(token.value, token.type)).as(Json.class).ok();
    } catch (ErrorResponseException e) {
      ErrorResponseDTO dto = new ErrorResponseDTO(e.code, e.description);
      return Reply.with(dto).status(SC_BAD_REQUEST).as(Json.class);
    }
  }

  private TokenRequestDTO read(Request request) {
    String grantType = request.param("grant_type");
    String username = request.param("username");
    String password = request.param("password");

    return new TokenRequestDTO(grantType, username, password);
  }

  private TokenRequest adapt(TokenRequestDTO dto) {
    return new TokenRequest(dto.getGrantType(), dto.getUsername(), dto.getPassword());
  }
}