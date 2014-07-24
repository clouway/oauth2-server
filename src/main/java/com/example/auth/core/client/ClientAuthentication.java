package com.example.auth.core.client;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface ClientAuthentication {
  Boolean authenticate(String id, String secret);
}