package com.example.auth.http;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class SecuredResourcesTest {
  private SecuredResources resources = new SecuredResources();

  @Test
  public void checkExistingResource() throws Exception {
    resources.add("/login");
    resources.add("/register");

    assertTrue(resources.contains("/register"));
  }

  @Test
  public void checkNotExistingResource() throws Exception {
    assertFalse(resources.contains("/login"));
  }
}