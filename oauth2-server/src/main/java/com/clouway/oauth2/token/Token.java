package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public final class Token implements Serializable {
  public final String value;
  public final TokenType type;
  public final GrantType grantType;
  public final String refreshToken;
  public final String identityId;
  public final String clientId;
  public final Long expiresInSeconds;
  public final DateTime creationDate;

  public Token() {
    this(null, null, null, null, null, null, null, null);
  }

  public Token(String value, TokenType type, GrantType grantType, String refreshToken, String identityId, String clientId, Long expiresInSeconds, DateTime creationDate) {
    this.value = value;
    this.type = type;
    this.grantType = grantType;
    this.refreshToken = refreshToken;
    this.identityId = identityId;
    this.clientId = clientId;
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
    if (o == null || getClass() != o.getClass()) return false;
    Token token = (Token) o;
    return Objects.equal(value, token.value) &&
            type == token.type &&
            Objects.equal(refreshToken, token.refreshToken) &&
            Objects.equal(identityId, token.identityId) &&
            Objects.equal(clientId, token.clientId) &&
            Objects.equal(expiresInSeconds, token.expiresInSeconds) &&
            Objects.equal(creationDate, token.creationDate);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value, clientId);
  }
}