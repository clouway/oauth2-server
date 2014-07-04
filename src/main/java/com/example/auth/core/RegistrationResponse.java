package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class RegistrationResponse {
  public final String clientId;
  public final String clientSecret;

  public RegistrationResponse(String clientId, String clientSecret) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }
}