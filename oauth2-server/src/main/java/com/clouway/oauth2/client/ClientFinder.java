package com.clouway.oauth2.client;

import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface ClientFinder {

  Optional<Client> findClient(String clientId);
}
