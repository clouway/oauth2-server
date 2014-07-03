package com.example.auth.core;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthorizationRequest {
  public final String responseType;
  public final String clientId;

  public AuthorizationRequest(String responseType, String clientId) {
    this.responseType = responseType;
    this.clientId = clientId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AuthorizationRequest that = (AuthorizationRequest) o;

    if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
    if (responseType != null ? !responseType.equals(that.responseType) : that.responseType != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = responseType != null ? responseType.hashCode() : 0;
    result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
    return result;
  }
}