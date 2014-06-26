package com.example.auth.core;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public abstract class ResourceOwnerRepositoryContractTest {
  private ResourceOwnerRepository repository = create();

  @Test
  public void correctCredentials() throws Exception {
    ResourceOwner owner = new ResourceOwner("username", "password");

    repository.save(owner);

    Boolean exists = repository.exist(owner.username, owner.password);

    assertTrue(exists);
  }

  @Test
  public void notExistingUser() throws Exception {
    Boolean exists = repository.exist("joro", "123456");

    assertFalse(exists);
  }

  @Test
  public void wrongPassword() throws Exception {
    ResourceOwner owner = new ResourceOwner("username", "pass");

    repository.save(owner);

    Boolean exists = repository.exist("username", "123456");

    assertFalse(exists);
  }

  protected abstract ResourceOwnerRepository create();
}