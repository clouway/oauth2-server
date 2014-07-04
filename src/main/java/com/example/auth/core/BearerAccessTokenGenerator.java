package com.example.auth.core;

import com.google.common.hash.Hashing;

import java.util.UUID;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class BearerAccessTokenGenerator implements AccessTokenGenerator {
  @Override
  public Token generate() {
    String value = Hashing.sha1().hashString(UUID.randomUUID().toString()).toString();
    String type = "bearer";

    return new Token(value, type);
  }
}