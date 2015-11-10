package com.example.auth.app.security;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class SecuredResources {
  private final Set<String> resources;

  public SecuredResources(Set<String> resources) {
    this.resources = ImmutableSet.copyOf(resources);
  }

  public Boolean contains(String resource) {
    return resources.contains(resource);
  }
}