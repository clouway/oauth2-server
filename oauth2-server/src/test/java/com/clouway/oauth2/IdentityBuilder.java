package com.clouway.oauth2;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Ianislav Nachev <qnislav.nachev@gmail.com>
 */
public class IdentityBuilder {
  Map<String, Object> claims = new LinkedHashMap<>();

  public static IdentityBuilder aNewIdentity() {
    return new IdentityBuilder();
  }

  private String id = "";

  public IdentityBuilder withId(String id) {
    this.id = id;
    return this;
  }

  public Identity build() {
    return new Identity(id, "", "", "", "", "", claims);
  }
}
