package com.clouway.oauth2;

import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.GrantType;

import java.util.Collections;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class TokenBuilder {


  public static TokenBuilder aNewToken() {
    return new TokenBuilder();
  }

  private String clientId = "";
  private DateTime expiresAt = new DateTime();
  private String value;

  public TokenBuilder withValue(String value) {
    this.value = value;
    return this;
  }

  public TokenBuilder expiresAt(DateTime expiresAt) {
    this.expiresAt = expiresAt;
    return this;
  }

  public BearerToken build() {
    return new BearerToken(value, GrantType.AUTHORIZATION_CODE, "", clientId, Collections.<String>emptySet(), expiresAt);
  }

  public TokenBuilder forClient(String clientId) {
    this.clientId = clientId;
    return this;
  }
}
