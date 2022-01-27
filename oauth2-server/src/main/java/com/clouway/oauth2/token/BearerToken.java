package com.clouway.oauth2.token;

import com.clouway.oauth2.common.DateTime;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public final class BearerToken implements Serializable {
  public final String value;
  public final GrantType grantType;
  public final String identityId;
  public final String clientId;
  public final String email;
  public final Map<String, String> params;
  public final Set<String> scopes;
  private final DateTime expiresAt;

  public BearerToken() {
    this(null, null, null, null, null, null, null, null);
  }

  public BearerToken(String value, GrantType grantType, String identityId, String clientId, String email, Set<String> scopes, DateTime expiresAt, Map<String, String> params) {
    this.value = value;
    this.grantType = grantType;
    this.identityId = identityId;
    this.clientId = clientId;
    this.email = email;
    this.scopes = scopes;
    this.expiresAt = expiresAt;
    this.params = params;
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
    BearerToken token = (BearerToken) o;
    return Objects.equal(value, token.value) &&
            Objects.equal(identityId, token.identityId) &&
            Objects.equal(clientId, token.clientId) &&
            Objects.equal(expiresAt, token.expiresAt);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value, clientId);
  }
}