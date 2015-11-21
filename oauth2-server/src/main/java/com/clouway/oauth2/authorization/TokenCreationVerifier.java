package com.clouway.oauth2.authorization;

import com.google.inject.ImplementedBy;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@ImplementedBy(TokenCreationVerifierImpl.class)
public interface TokenCreationVerifier {

  Boolean verify(String code, String clintId);

}