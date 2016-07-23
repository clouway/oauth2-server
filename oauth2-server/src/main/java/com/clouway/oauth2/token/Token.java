package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;

import java.io.Serializable;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public final class Token implements Serializable {
  public final String value;
  public final TokenType type;
  public final String refreshToken;
  public final String identityId;
  public final Long expiresInSeconds;
  public final DateTime creationDate;

  public Token() {
    this(null, null, null, null, null, null);
  }

  public Token(String value, TokenType type, String refreshToken, String identityId, Long expiresInSeconds, DateTime creationDate) {
    this.value = value;
    this.type = type;
    this.refreshToken = refreshToken;
    this.identityId = identityId;
    this.expiresInSeconds = expiresInSeconds;
    this.creationDate = creationDate;
  }

  public boolean expiresAt(DateTime date) {
    return date.after(creationDate.plusSeconds(expiresInSeconds));
  }

  public Long expirationTimestamp() {
    DateTime expirationTime = creationDate.plusSeconds(expiresInSeconds);
    return expirationTime.timestamp();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Token)) return false;

    Token token = (Token) o;

    if (creationDate != null ? !creationDate.equals(token.creationDate) : token.creationDate != null) return false;
    if (expiresInSeconds != null ? !expiresInSeconds.equals(token.expiresInSeconds) : token.expiresInSeconds != null)
      return false;
    if (type != null ? !type.equals(token.type) : token.type != null) return false;
    if (value != null ? !value.equals(token.value) : token.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (expiresInSeconds != null ? expiresInSeconds.hashCode() : 0);
    result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
    return result;
  }
}