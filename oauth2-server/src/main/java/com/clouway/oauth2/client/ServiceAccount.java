package com.clouway.oauth2.client;

/**
 * ServiceAccount is a representing ServiceAccount.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class ServiceAccount {

  private final String clientId;
  private final String privateKeyPem;

  public ServiceAccount(String clientId, String privateKeyPem) {
    this.clientId = clientId;
    this.privateKeyPem = privateKeyPem;
  }

  public String privateKey() {
    return privateKeyPem;
  }

  public String clientId() {
    return clientId;
  }
}
