package com.example.auth.gae;

import com.example.auth.http.OauthAuthorizationServerModule;
import com.google.inject.AbstractModule;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class GaeOauthAuthorizationModule extends AbstractModule {
  private String url = "";

  public GaeOauthAuthorizationModule() {
  }

  public GaeOauthAuthorizationModule(String url) {
    this.url = url;
  }

  @Override
  protected void configure() {

    install(new OauthAuthorizationServerModule(url));

  }
}
