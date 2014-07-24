package com.example.auth.core.token;

import com.google.common.hash.Hashing;
import com.google.inject.ImplementedBy;

import java.util.UUID;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@ImplementedBy(Sha1TokenGenerator.class)
public class Sha1TokenGenerator implements TokenGenerator {
  @Override
  public String generate() {
    return Hashing.sha1().hashBytes(UUID.randomUUID().toString().getBytes()).toString();
  }
}