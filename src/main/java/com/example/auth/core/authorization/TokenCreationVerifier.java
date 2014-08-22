package com.example.auth.core.authorization;

import com.google.inject.ImplementedBy;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@ImplementedBy(TokenCreationVerifierImpl.class)
public interface TokenCreationVerifier {

  Boolean verify(String code, String clintId);

  Boolean verifyRefreshToken(String clientId, String clientSecret, String refreshToken);
}