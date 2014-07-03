package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ClientRegister {
  RegistrationResponse register(RegistrationRequest request);
}