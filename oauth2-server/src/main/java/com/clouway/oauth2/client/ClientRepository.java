package com.clouway.oauth2.client;

import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface ClientRepository {

  Client register(ClientRegistrationRequest request) throws ClientAlreadyExistsException;

  Optional<Client> findById(String id);
}
