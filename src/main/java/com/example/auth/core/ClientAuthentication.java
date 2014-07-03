package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ClientAuthentication {
  Boolean authenticate(String id, String secret);
}