package com.clouway.oauth2.authorization;

import com.clouway.oauth2.codechallenge.CodeChallenge;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Authorization {
  public final String responseType;
  public final String clientId;
  public final String code;
  public final Set<String> scopes;
  public final Set<String> redirectUrls;
  public final String identityId;
  public final CodeChallenge codeChallenge;
  public final Map<String, String> params;

  private Date usageDate = null;

  public Authorization(String responseType, String clientId, String identityId, String code, Set<String> scopes, Set<String> redirectUrls, CodeChallenge codeChallenge, Map<String, String> params) {
    this.responseType = responseType;
    this.clientId = clientId;
    this.code = code;
    this.scopes = scopes;
    this.redirectUrls = redirectUrls;
    this.identityId = identityId;
    this.codeChallenge = codeChallenge;
    this.params = params;
  }

  public Authorization(String responseType, String clientId, String identityId, String code, Set<String> scopes, Set<String> redirectUrls,  Map<String, String> params) {
    this.responseType = responseType;
    this.clientId = clientId;
    this.code = code;
    this.scopes = scopes;
    this.redirectUrls = redirectUrls;
    this.identityId = identityId;
    this.codeChallenge = null;
    this.params = params;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Authorization that = (Authorization) o;
    return Objects.equals(responseType, that.responseType) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(code, that.code) &&
            Objects.equals(scopes, that.scopes) &&
            Objects.equals(redirectUrls, that.redirectUrls) &&
            Objects.equals(identityId, that.identityId) &&
            Objects.equals(usageDate, that.usageDate) &&
            Objects.equals(params, that.params);

  }

  @Override
  public int hashCode() {
    return Objects.hash(responseType, clientId, code, redirectUrls, identityId, usageDate);
  }


}