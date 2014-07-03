package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenErrorResponse extends RuntimeException {
  public final String code;
  public final String description;

  public TokenErrorResponse(String code, String description) {
    this.code = code;
    this.description = description;
  }
}