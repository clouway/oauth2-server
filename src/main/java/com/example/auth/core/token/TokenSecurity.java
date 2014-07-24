package com.example.auth.core.token;

import com.google.inject.ImplementedBy;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@ImplementedBy(TokenSecurityImpl.class)
public interface TokenSecurity {
  Token create(TokenRequest request);
}