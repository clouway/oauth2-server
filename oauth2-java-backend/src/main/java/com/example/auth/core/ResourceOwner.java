package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class ResourceOwner {
  public final String username;
  public final String password;

  public ResourceOwner(String username, String password) {
    this.username = username;
    this.password = password;
  }
}