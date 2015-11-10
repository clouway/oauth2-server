package com.example.auth.core.token;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenRequest {
  public final String grantType;
  public final String code;
  public final String refreshToken;
  public final String clientId;
  public final String clientSecret;

  public TokenRequest(String grantType, String code, String refreshToken, String clientId, String clientSecret) {
    this.grantType = grantType;
    this.code = code;
    this.refreshToken = refreshToken;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TokenRequest that = (TokenRequest) o;

    if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
    if (clientSecret != null ? !clientSecret.equals(that.clientSecret) : that.clientSecret != null) return false;
    if (code != null ? !code.equals(that.code) : that.code != null) return false;
    if (grantType != null ? !grantType.equals(that.grantType) : that.grantType != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = grantType != null ? grantType.hashCode() : 0;
    result = 31 * result + (code != null ? code.hashCode() : 0);
    result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
    result = 31 * result + (clientSecret != null ? clientSecret.hashCode() : 0);
    return result;
  }
}