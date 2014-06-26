package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface TokenRepository {
  Token create();

  Boolean verify(String token);
}