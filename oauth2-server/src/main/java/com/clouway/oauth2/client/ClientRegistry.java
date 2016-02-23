package com.clouway.oauth2.client;

import com.google.inject.ImplementedBy;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@ImplementedBy(ClientRegistryImpl.class)
public interface ClientRegistry {
  RegistrationResponse register(RegistrationRequest request);
}