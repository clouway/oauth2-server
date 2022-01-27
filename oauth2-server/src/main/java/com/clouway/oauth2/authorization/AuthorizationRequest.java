package com.clouway.oauth2.authorization;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.codechallenge.CodeChallenge;
import com.google.common.base.Objects;

import java.util.Map;
import java.util.Set;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public final class AuthorizationRequest {
  public final Client client;
  public final String identityId;
  public final String responseType;
  public final Set<String> scopes;
  public final Map<String, String> params;
  //Nullable
  public final CodeChallenge codeChallenge;

  public AuthorizationRequest(Client client, String identityId, String responseType, Set<String> scopes, CodeChallenge codeChallenge, Map<String, String> params) {
    this.client = client;
    this.identityId = identityId;
    this.responseType = responseType;
    this.scopes = scopes;
    this.codeChallenge = codeChallenge;
    this.params = params;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthorizationRequest that = (AuthorizationRequest) o;
    return Objects.equal(client, that.client) &&
            Objects.equal(identityId, that.identityId) &&
            Objects.equal(responseType, that.responseType) &&
            Objects.equal(scopes, that.scopes) &&
            Objects.equal(params, that.params) &&
            Objects.equal(codeChallenge, that.codeChallenge);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(client, identityId, responseType, scopes, params, codeChallenge);
  }
}
