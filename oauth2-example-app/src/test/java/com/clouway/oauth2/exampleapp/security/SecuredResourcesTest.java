package com.clouway.oauth2.exampleapp.security;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class SecuredResourcesTest {
  private SecuredResources resources;

  @Test
  public void checkExistingResource() throws Exception {
    resources = new SecuredResources(Sets.newHashSet("/login","/register"));
    assertTrue(resources.contains("/register"));
  }

  @Test
  public void checkNotExistingResource() throws Exception {
    resources = new SecuredResources(new HashSet<String>());
    assertFalse(resources.contains("/login"));
  }
}