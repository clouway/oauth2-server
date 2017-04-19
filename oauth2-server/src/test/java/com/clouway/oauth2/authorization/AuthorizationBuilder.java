package com.clouway.oauth2.authorization;

import java.util.Collections;
import java.util.Set;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class AuthorizationBuilder {
  private String responseType = "code";
  private String code = "::code::";
  private String clientId = "::client id::";
  private Set<String> scopes = Collections.singleton("scope1");
  private Set<String> redirectURIs = Collections.singleton("redirectURI");
  private String identityId = "::any identity::";

  public static AuthorizationBuilder newAuthorization() {
    return new AuthorizationBuilder();
  }

  public Authorization build() {
    return new Authorization(responseType, clientId, identityId, code, scopes, redirectURIs);
  }

}