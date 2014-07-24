package com.example.auth.http.security;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class SecuredResources {
  private final Set<String> resources = new HashSet<String>();

  public void add(String resource) {
    resources.add(resource);
  }

  public Boolean contains(String resource) {
    return resources.contains(resource);
  }
}