package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface TokenSecurity {
  Token create(TokenRequest request);
}