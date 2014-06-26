package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class ErrorResponseException extends RuntimeException {
  public final String code;
  public final String description;

  public ErrorResponseException(String code, String description) {
    this.code = code;
    this.description = description;
  }
}