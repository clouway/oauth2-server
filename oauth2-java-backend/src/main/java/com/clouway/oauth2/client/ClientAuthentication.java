package com.clouway.oauth2.client;

import com.google.inject.ImplementedBy;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@ImplementedBy(ClientAuthenticationImpl.class)
public interface ClientAuthentication {
  Boolean authenticate(String id, String secret);
}