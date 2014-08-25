package com.example.auth.core.authorization;

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