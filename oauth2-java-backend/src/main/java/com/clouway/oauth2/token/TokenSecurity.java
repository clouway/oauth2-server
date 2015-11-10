package com.clouway.oauth2.token;


import com.google.inject.ImplementedBy;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@ImplementedBy(TokenSecurityImpl.class)
public interface TokenSecurity {

  void validate(ProvidedAuthorizationCode authCode);

  void authenticateClient(String clientId, String clientSecret);
}