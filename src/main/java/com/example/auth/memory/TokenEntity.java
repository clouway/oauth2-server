package com.example.auth.memory;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class TokenEntity {
  final String value;
  final String type;
  final Date expiration;

  public TokenEntity(String value, String type, Date expiration) {
    this.value = value;
    this.type = type;
    this.expiration = expiration;
  }
}