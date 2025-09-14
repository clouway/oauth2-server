package com.clouway.oauth2.token;

import com.clouway.oauth2.common.DateTime;
import com.google.common.base.Objects;

import java.util.Map;

/**
 * @author Ianislav Nachev <qnislav.nachev@clouway.com>
 */
public final class FindIdentityRequest {
  public final SubjectKind subjectKind;
  public final String identityId;
  public final GrantType grantType;
  public final DateTime instantTime;
  public final Map<String, String> params;
  public final String clientId;

  public FindIdentityRequest(String identityId, GrantType grantType, DateTime instantTime, Map<String, String> params, String clientId) {
    this.identityId = identityId;
    this.grantType = grantType;
    this.instantTime = instantTime;
    this.params = params;
    this.clientId = clientId;
    String sk = params != null ? params.get("subject_kind") : null;
    if ("service_account".equalsIgnoreCase(sk)) {
      this.subjectKind = SubjectKind.SERVICE_ACCOUNT;
    } else if ("user".equalsIgnoreCase(sk)) {
      this.subjectKind = SubjectKind.USER;
    } else {
      // Fallback to grant type for backward compatibility
      this.subjectKind = (grantType == GrantType.JWT) ? SubjectKind.SERVICE_ACCOUNT : SubjectKind.USER;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FindIdentityRequest that = (FindIdentityRequest) o;
    return Objects.equal(identityId, that.identityId) &&
            grantType == that.grantType &&
            Objects.equal(instantTime, that.instantTime) &&
            Objects.equal(params, that.params) &&
            Objects.equal(clientId, that.clientId) &&
            subjectKind == that.subjectKind;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(identityId, grantType, instantTime, params, clientId, subjectKind);
  }
}
