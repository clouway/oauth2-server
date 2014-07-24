package com.example.auth.core.authorization;

import com.google.inject.ImplementedBy;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@ImplementedBy(ClientAuthorizationRequestVerifierImpl.class)
public interface ClientAuthorizationRequestVerifier {

  Boolean verify(String code, String clintId);

}