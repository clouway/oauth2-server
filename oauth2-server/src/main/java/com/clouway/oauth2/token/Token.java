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
  public final String identityId;
  public final String clientId;
  private final DateTime expiresAt;

  public Token() {
    this(null, null, null, null, null, null);
  }

  public Token(String value, TokenType type, GrantType grantType, String identityId, String clientId, DateTime expiresAt) {
    this.value = value;
    this.type = type;
    this.grantType = grantType;
    this.identityId = identityId;
    this.clientId = clientId;
    this.expiresAt = expiresAt;
  }

  /**
   * Checks whether the Token expires at the provided instant time.
   *
   * @param instant the instant to against witch instant is checked
   * @return true if token expires at the provided time and false in other case
   */
  public boolean expiresAt(DateTime instant) {
    return instant.after(expiresAt);
  }

  /**
   * Expiration time as timestamp value.
   *
   * @return the expiration time as timestamp
   */
  public Long expirationTimestamp() {
    return expiresAt.timestamp();
  }

  /**
   * Gets time to live in seconds of the current token.
   *
   * @param instant the instant time used for check
   * @return the time to live in seconds
   */
  public Long ttlSeconds(DateTime instant) {
    return (expiresAt.timestamp() - instant.timestamp()) / 1000;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Token token = (Token) o;
    return Objects.equal(value, token.value) &&
            type == token.type &&
            Objects.equal(identityId, token.identityId) &&
            Objects.equal(clientId, token.clientId) &&
            Objects.equal(expiresAt, token.expiresAt);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value, clientId);
  }
}