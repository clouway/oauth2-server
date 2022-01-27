package com.clouway.oauth2.token;

import com.clouway.oauth2.common.DateTime;

import java.util.Collections;
import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class BearerTokenBuilder {

  public static BearerTokenBuilder aNewToken() {
    return new BearerTokenBuilder();
  }

  private String clientId = "";
  private String identityId = "";
  private GrantType grantType = GrantType.AUTHORIZATION_CODE;
  private DateTime expiresAt = new DateTime();
  private String value;
  private String email = "";
  private Map<String, String> params = Collections.emptyMap();

  public BearerTokenBuilder withValue(String value) {
    this.value = value;
    return this;
  }

  public BearerTokenBuilder expiresAt(DateTime expiresAt) {
    this.expiresAt = expiresAt;
    return this;
  }

  public BearerTokenBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public BearerTokenBuilder params(Map<String, String> params) {
    this.params = params;
    return this;
  }

  public BearerTokenBuilder identityId(String identityId){
    this.identityId = identityId;
    return this;
  }

  public BearerTokenBuilder grantType(GrantType grantType){
    this.grantType = grantType;
    return this;
  }

  public BearerToken build() {
    return new BearerToken(value, grantType, identityId, clientId, email, Collections.<String>emptySet(), expiresAt, params);
  }

  public BearerTokenBuilder forClient(String clientId) {
    this.clientId = clientId;
    return this;
  }
}
