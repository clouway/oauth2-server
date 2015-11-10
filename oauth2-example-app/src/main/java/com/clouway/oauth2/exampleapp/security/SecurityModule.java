package com.clouway.oauth2.exampleapp.security;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class SecurityModule extends AbstractModule {

  private String url = "";
  private String loginPageUrl = "";

  public SecurityModule(String url) {
    this.url = url;
  }

  public SecurityModule(String url, String loginPageUrl) {
    this.url = url;
    this.loginPageUrl = loginPageUrl;
  }

  protected void configure() {
    install(new ServletModule() {
      @Override
      protected void configureServlets() {
        filter(url + "/*").through(OauthSecurityFilter.class);
      }
    });

  }

  @Provides
  @Singleton
  public SecuredResources provideSecuredResources() {
    return new SecuredResources(Sets.newHashSet(url + "/authorize"));
  }

  @Provides
  @UriPath
  public String uriPath() {
    return url;
  }

  @Provides
  @LoginPageUrl
  public String loginPageUrl() {
    return loginPageUrl;
  }
}
