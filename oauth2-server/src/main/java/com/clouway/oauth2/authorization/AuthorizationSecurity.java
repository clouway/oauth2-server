package com.clouway.oauth2.authorization;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface AuthorizationSecurity {
  AuthorizationResponse auth(AuthorizationRequest request);
}