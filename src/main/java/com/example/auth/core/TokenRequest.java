package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenRequest {
  public final String grantType;
  public final String username;
  public final String password;

  public TokenRequest(String grantType, String username, String password) {
    this.grantType = grantType;
    this.username = username;
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TokenRequest request = (TokenRequest) o;

    if (grantType != null ? !grantType.equals(request.grantType) : request.grantType != null) return false;
    if (password != null ? !password.equals(request.password) : request.password != null) return false;
    if (username != null ? !username.equals(request.username) : request.username != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = grantType != null ? grantType.hashCode() : 0;
    result = 31 * result + (username != null ? username.hashCode() : 0);
    result = 31 * result + (password != null ? password.hashCode() : 0);
    return result;
  }
}