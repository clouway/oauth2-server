package com.clouway.oauth2.authorization;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
    try {
      return redirectURI + "?code=" + URLEncoder.encode(code, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return  redirectURI + "?code=" + code;
    }
  }
}