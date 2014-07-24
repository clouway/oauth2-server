package com.example.auth.core.authorization;

import com.example.auth.core.AuthorizationRequest;
import com.example.auth.core.AuthorizationResponse;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface AuthorizationSecurity {
  AuthorizationResponse auth(AuthorizationRequest request);
}