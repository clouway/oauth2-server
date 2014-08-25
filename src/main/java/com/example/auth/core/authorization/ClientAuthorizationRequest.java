package com.example.auth.core.authorization;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class ClientAuthorizationRequest {
  public final String responseType;
  public final String clientId;
  public final String code;
  public final String redirectURI;
  private Date usageDate = null;

  public ClientAuthorizationRequest(String responseType, String clientId, String code, String redirectURI) {
    this.responseType = responseType;
    this.clientId = clientId;
    this.code = code;
    this.redirectURI = redirectURI;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ClientAuthorizationRequest that = (ClientAuthorizationRequest) o;

    if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
    if (code != null ? !code.equals(that.code) : that.code != null) return false;
    if (redirectURI != null ? !redirectURI.equals(that.redirectURI) : that.redirectURI != null) return false;
    if (responseType != null ? !responseType.equals(that.responseType) : that.responseType != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = responseType != null ? responseType.hashCode() : 0;
    result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
    result = 31 * result + (code != null ? code.hashCode() : 0);
    result = 31 * result + (redirectURI != null ? redirectURI.hashCode() : 0);
    return result;
  }

  public void usedOn(Date date) {
    usageDate = date;
  }

  public boolean isNotUsed() {
    return usageDate == null;
  }
}