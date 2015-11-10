package com.example.auth.core.token;

import com.google.inject.ImplementedBy;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@ImplementedBy(TokenVerifierImpl.class)
public interface TokenVerifier {
  Boolean verify(String token);
}