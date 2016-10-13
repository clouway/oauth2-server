package com.clouway.oauth2.authorization;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Authorization {
  public final String responseType;
  public final String clientId;
  public final String code;
  public final Set<String> redirectUrls;
  public final String identityId;

  private Date usageDate = null;

  public Authorization(String responseType, String clientId, String code, Set<String> redirectUrls, String identityId) {
    this.responseType = responseType;
    this.clientId = clientId;
    this.code = code;
    this.redirectUrls = redirectUrls;
    this.identityId = identityId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Authorization that = (Authorization) o;
    return Objects.equals(responseType, that.responseType) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(code, that.code) &&
            Objects.equals(redirectUrls, that.redirectUrls) &&
            Objects.equals(identityId, that.identityId) &&
            Objects.equals(usageDate, that.usageDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(responseType, clientId, code, redirectUrls, identityId, usageDate);
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