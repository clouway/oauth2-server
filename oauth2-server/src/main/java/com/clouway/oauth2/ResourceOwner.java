package com.clouway.oauth2;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public final class ResourceOwner {
  public final String username;
  public final String password;

  public ResourceOwner(String username, String password) {
    this.username = username;
    this.password = password;
  }
}