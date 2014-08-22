package com.example.auth.core.token.refreshtoken;

import java.util.Date;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class RefreshToken {

  public static Builder aNewRefreshToken(String value, String clientId, String secret) {
    return new Builder(value, clientId, secret);
  }

  public static class Builder {

    private String value;
    private String clientId;
    private String secret;
    private Date creationDate;

    public Builder(String value, String clientId, String secret) {
      this.value = value;
      this.clientId = clientId;
      this.secret = secret;
    }

    public Builder creationDate(Date creationDate) {
      this.creationDate = creationDate;
      return this;
    }


    public RefreshToken build() {
      RefreshToken t = new RefreshToken(value, clientId, secret);
      t.creationDate = creationDate;
      return t;
    }

  }

  public final String value;
  public final String clientId;
  public final String secret;
  private Date creationDate;

  public RefreshToken(String value, String clientId, String secret) {
    this.value = value;
    this.clientId = clientId;
    this.secret = secret;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RefreshToken)) return false;

    RefreshToken token = (RefreshToken) o;

    if (clientId != null ? !clientId.equals(token.clientId) : token.clientId != null) return false;
    if (secret != null ? !secret.equals(token.secret) : token.secret != null) return false;
    if (value != null ? !value.equals(token.value) : token.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
    result = 31 * result + (secret != null ? secret.hashCode() : 0);
    return result;
  }
}
