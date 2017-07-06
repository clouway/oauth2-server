package com.clouway.oauth2;

import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.GrantType;

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
  private DateTime expiresAt = new DateTime();
  private String value;
  private String email = "";
  private Map<String, String> params;

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

  public BearerToken build() {
    return new BearerToken(value, GrantType.AUTHORIZATION_CODE, "", clientId, email, Collections.<String>emptySet(), expiresAt, params);
  }

  public BearerTokenBuilder forClient(String clientId) {
    this.clientId = clientId;
    return this;
  }
}
