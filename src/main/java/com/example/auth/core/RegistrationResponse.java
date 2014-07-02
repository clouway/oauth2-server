package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class RegistrationResponse {
  public final String id;
  public final String secret;

  public RegistrationResponse(String id, String secret) {
    this.id = id;
    this.secret = secret;
  }
}