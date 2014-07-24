package com.example.auth.memory;

import com.example.auth.core.client.Client;
import com.example.auth.core.client.ClientAuthentication;
import com.example.auth.core.client.ClientAuthenticationContractTest;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class InMemoryClientAuthenticationTest extends ClientAuthenticationContractTest {

  private InMemoryClientRepository repository = new InMemoryClientRepository();

  @Override
  protected ClientAuthentication createClientAuthentication() {
    return repository;
  }

  @Override
  protected void save(Client client) {
    repository.save(client);
  }
}
