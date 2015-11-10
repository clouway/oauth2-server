package com.clouway.oauth2.authorization;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthorizationResponse {
  private final String code;
  private final String redirectURI;

  public AuthorizationResponse(String code, String redirectURI) {
    this.code = code;
    this.redirectURI = redirectURI;
  }

  public String buildURI() {
    return redirectURI + "?code=" + code;
  }
}