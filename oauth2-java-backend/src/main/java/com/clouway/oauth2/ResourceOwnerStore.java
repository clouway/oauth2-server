package com.clouway.oauth2;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ResourceOwnerStore {
  void save(ResourceOwner resourceOwner);
}