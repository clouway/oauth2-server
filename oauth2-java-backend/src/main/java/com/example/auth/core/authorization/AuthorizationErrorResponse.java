package com.example.auth.core.authorization;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthorizationErrorResponse extends RuntimeException {
  public final String description;

  public AuthorizationErrorResponse(String description) {
    this.description = description;
  }
}