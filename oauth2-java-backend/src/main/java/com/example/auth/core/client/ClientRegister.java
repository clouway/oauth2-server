package com.example.auth.core.client;

import com.google.inject.ImplementedBy;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@ImplementedBy(ClientRegisterImpl.class)
public interface ClientRegister {
  RegistrationResponse register(RegistrationRequest request);
}