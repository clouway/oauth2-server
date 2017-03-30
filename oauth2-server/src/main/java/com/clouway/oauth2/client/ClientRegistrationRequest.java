package com.clouway.oauth2.client;

import java.util.Set;

public class ClientRegistrationRequest {
  public final String secret;
  public final String description;
  public final Set<String> redirectURLs;

  public ClientRegistrationRequest(String secret, String description, Set<String> redirectURLs) {
    this.secret = secret;
    this.description = description;
    this.redirectURLs = redirectURLs;
  }
}