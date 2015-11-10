package com.example.auth.core.authorization;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthorizationRequest {
  public final String responseType;
  public final String clientId;
  public final String sessionId;

  public AuthorizationRequest(String responseType, String clientId, String sessionId) {
    this.responseType = responseType;
    this.clientId = clientId;
    this.sessionId = sessionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AuthorizationRequest)) return false;

    AuthorizationRequest that = (AuthorizationRequest) o;

    if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
    if (responseType != null ? !responseType.equals(that.responseType) : that.responseType != null) return false;
    if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = responseType != null ? responseType.hashCode() : 0;
    result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
    result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
    return result;
  }
}