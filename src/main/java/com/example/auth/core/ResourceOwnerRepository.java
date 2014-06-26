package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ResourceOwnerRepository {
  void save(ResourceOwner resourceOwner);

  Boolean exist(String username, String password);
}