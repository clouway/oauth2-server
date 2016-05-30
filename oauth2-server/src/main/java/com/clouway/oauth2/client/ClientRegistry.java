package com.clouway.oauth2.client;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ClientRegistry {
  RegistrationResponse register(RegistrationRequest request) throws ClientAlreadyExistsException;
}