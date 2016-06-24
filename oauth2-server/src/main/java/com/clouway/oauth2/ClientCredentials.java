package com.clouway.oauth2;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class ClientCredentials {
  private String clientId;
  private String clientSecret;

  public ClientCredentials(String clientId, String clientSecret) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  public String clientId() {
    return clientId;
  }

  public String clientSecret() {
    return clientSecret;
  }
}
