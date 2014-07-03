package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface AuthorizationVerifier {
  Boolean verify(String code, String clintId);
}