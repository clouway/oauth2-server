package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface AuthorizationSecurity {
  AuthorizationResponse auth(AuthorizationRequest request);
}