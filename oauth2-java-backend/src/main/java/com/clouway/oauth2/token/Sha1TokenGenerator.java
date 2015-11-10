package com.clouway.oauth2.token;

import com.google.common.hash.Hashing;

import java.util.UUID;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Sha1TokenGenerator implements TokenGenerator {
  @Override
  public String generate() {
    return Hashing.sha1().hashBytes(UUID.randomUUID().toString().getBytes()).toString();
  }
}