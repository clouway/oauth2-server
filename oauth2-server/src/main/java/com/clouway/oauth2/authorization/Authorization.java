package com.clouway.oauth2.authorization;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Authorization {
  public final String responseType;
  public final String clientId;
  public final String code;
  public final String redirectURI;
  public final String identityId;

  private Date usageDate = null;

  public Authorization(String responseType, String clientId, String code, String redirectURI, String identityId) {
    this.responseType = responseType;
    this.clientId = clientId;
    this.code = code;
    this.redirectURI = redirectURI;
    this.identityId = identityId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Authorization)) return false;

    Authorization that = (Authorization) o;

    if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
    if (code != null ? !code.equals(that.code) : that.code != null) return false;
    if (redirectURI != null ? !redirectURI.equals(that.redirectURI) : that.redirectURI != null) return false;
    if (responseType != null ? !responseType.equals(that.responseType) : that.responseType != null) return false;
    if (usageDate != null ? !usageDate.equals(that.usageDate) : that.usageDate != null) return false;
    if (identityId != null ? !identityId.equals(that.identityId) : that.identityId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = responseType != null ? responseType.hashCode() : 0;
    result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
    result = 31 * result + (code != null ? code.hashCode() : 0);
    result = 31 * result + (redirectURI != null ? redirectURI.hashCode() : 0);
    result = 31 * result + (identityId != null ? identityId.hashCode() : 0);
    result = 31 * result + (usageDate != null ? usageDate.hashCode() : 0);
    return result;
  }

  public void usedOn(Date date) {
    usageDate = date;
  }

  public boolean wasAlreadyUsed() {
    return usageDate != null;
  }

  public boolean isNotUsed() {
    return usageDate == null;
  }

  public Date getUsedOnDate() {
    return usageDate;
  }
}