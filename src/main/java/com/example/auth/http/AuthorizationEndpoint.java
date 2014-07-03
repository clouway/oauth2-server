package com.example.auth.http;

import com.example.auth.core.AuthorizationErrorResponse;
import com.example.auth.core.AuthorizationRequest;
import com.example.auth.core.AuthorizationResponse;
import com.example.auth.core.AuthorizationSecurity;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Service
@At("/authorize")
public class AuthorizationEndpoint {
  private final AuthorizationSecurity authorizationSecurity;

  @Inject
  public AuthorizationEndpoint(AuthorizationSecurity authorizationSecurity) {
    this.authorizationSecurity = authorizationSecurity;
  }

  @Get
  public Reply<?> authorize(Request request) {
    try {
      AuthorizationRequest authorizationRequest = read(request);

      AuthorizationResponse response = authorizationSecurity.auth(authorizationRequest);

      return Reply.saying().redirect(response.buildURI()).ok();
    } catch (AuthorizationErrorResponse error) {
      return Reply.with(error.description).status(SC_BAD_REQUEST);
    }
  }

  private AuthorizationRequest read(Request request) {
    String responseType = request.param("response_type");
    String clientId = request.param("client_id");

    return new AuthorizationRequest(responseType, clientId);
  }
}