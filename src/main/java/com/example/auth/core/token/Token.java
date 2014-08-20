package com.example.auth.core.token;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Token {
  public final String value;
  public final String type;
  public final Date expiration;


  public Token(String value, String type, Date expirationTime) {
    this.value = value;
    this.type = type;
    this.expiration = expirationTime;
  }

  public Token expiresOn(Date expirationTime) {
    return new Token(value, type, expirationTime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Token)) return false;

    Token token = (Token) o;

    if (expiration != null ? !expiration.equals(token.expiration) : token.expiration != null) return false;
    if (type != null ? !type.equals(token.type) : token.type != null) return false;
    if (value != null ? !value.equals(token.value) : token.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (expiration != null ? expiration.hashCode() : 0);
    return result;
  }
}