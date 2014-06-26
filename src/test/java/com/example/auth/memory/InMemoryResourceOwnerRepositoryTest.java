package com.example.auth.memory;

import com.example.auth.core.ResourceOwnerRepository;
import com.example.auth.core.ResourceOwnerRepositoryContractTest;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryResourceOwnerRepositoryTest extends ResourceOwnerRepositoryContractTest {
  @Override
  public ResourceOwnerRepository create() {
    return new InMemoryResourceOwnerRepository();
  }
}