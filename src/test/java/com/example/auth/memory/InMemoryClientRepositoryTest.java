package com.example.auth.memory;

import com.example.auth.core.ClientRepository;
import com.example.auth.core.ClientRepositoryContractTest;
import com.example.auth.core.TokenGenerator;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryClientRepositoryTest extends ClientRepositoryContractTest {
  @Override
  protected ClientRepository create(TokenGenerator tokenGenerator) {
    return new InMemoryClientRepository(tokenGenerator);
  }
}