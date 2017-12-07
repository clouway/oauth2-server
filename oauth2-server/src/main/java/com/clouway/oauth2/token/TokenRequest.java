package com.clouway.oauth2.token;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.Identity;
import com.clouway.oauth2.client.Client;
import com.google.common.base.Objects;

import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public final class TokenRequest {

  public static final class Builder {
    private GrantType grantType;
    private Client client;
    private Identity identity;
    private Set<String> scopes;
    private DateTime when;
    private Map<String, String> params;

    private Builder() {
    }

    public TokenRequest build() {
      return new TokenRequest(this);
    }

    public Builder grantType(GrantType grantType) {
      this.grantType = grantType;
      return this;
    }

    public Builder client(Client client) {
      this.client = client;
      return this;
    }

    public Builder identity(Identity identity) {
      this.identity = identity;
      return this;
    }

    public Builder scopes(Set<String> scopes) {
      this.scopes = scopes;
      return this;
    }

    public Builder when(DateTime when) {
      this.when = when;
      return this;
    }

    public Builder params(Map<String, String> params) {
      this.params = params;
      return this;
    }
  }

  public final GrantType grantType;
  public final Client client;
  public final Identity identity;
  public final Set<String> scopes;
  public final DateTime when;
  public final Map<String, String> params;

  private TokenRequest(Builder builder) {
    this.grantType = builder.grantType;
    this.client = builder.client;
    this.identity = builder.identity;
    this.scopes = builder.scopes;
    this.when = builder.when;
    this.params = builder.params;
  }

  public static Builder newTokenRequest() {
    return new Builder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TokenRequest that = (TokenRequest) o;
    return grantType == that.grantType &&
            Objects.equal(client, that.client) &&
            Objects.equal(identity, that.identity) &&
            Objects.equal(scopes, that.scopes) &&
            Objects.equal(when, that.when) &&
            Objects.equal(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(grantType, client, identity, scopes, when, params);
  }
}

