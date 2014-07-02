package com.example.auth.core;

import com.google.common.base.Optional;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ClientRepository {
  RegistrationResponse register(RegistrationRequest request);

  Optional<Client> findById(String id);
}