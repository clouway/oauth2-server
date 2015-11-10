package com.example.auth.core.authorization;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface AuthorizationSecurity {
  AuthorizationResponse auth(AuthorizationRequest request);
}