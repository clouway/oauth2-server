package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface AuthorizationStore {
  void register(Authorization authorization);
}