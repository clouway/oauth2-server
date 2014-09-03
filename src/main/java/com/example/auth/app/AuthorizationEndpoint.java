package com.example.auth.app;

import com.example.auth.core.authorization.AuthorizationErrorResponse;
import com.example.auth.core.authorization.AuthorizationRequest;
import com.example.auth.core.authorization.AuthorizationResponse;
import com.example.auth.core.authorization.AuthorizationSecurity;
import com.google.inject.Inject;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Service
public class AuthorizationEndpoint {
  private final AuthorizationSecurity authorizationSecurity;

  @Inject
  public AuthorizationEndpoint(AuthorizationSecurity authorizationSecurity) {
    this.authorizationSecurity = authorizationSecurity;
  }

  @Get
  public Reply<?> authorize(HttpServletRequest httpRequest) {
    try {
      AuthorizationRequest authorizationRequest = read(httpRequest);

      AuthorizationResponse response = authorizationSecurity.auth(authorizationRequest);

      return Reply.saying().redirect(response.buildURI());
    } catch (AuthorizationErrorResponse error) {
      return Reply.with(error.description).status(SC_BAD_REQUEST);
    }
  }

  private AuthorizationRequest read(HttpServletRequest request) {
    String responseType = request.getParameter("response_type");
    String clientId = request.getParameter("client_id");
    String sessionId = getCookieValue(request, "SID");

    return new AuthorizationRequest(responseType, clientId, sessionId);
  }

  /**
     * Find and returns the value of the cookie with the provided name
     *
     * @param request http request from which cookie will be extracted
     * @param name    cookie name
     * @return the value of the cookie if found null otherwise
     */
    private String getCookieValue(HttpServletRequest request, String name) {
      Cookie[] cookies = request.getCookies();

      if (cookies != null) { //Stupid servlet API!
        for (Cookie cookie : cookies) {
          if (name.equals(cookie.getName())) {
            return cookie.getValue();
          }
        }
      }

      return "";
    }
}