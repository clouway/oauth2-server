package com.example.auth.http.security;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class OauthAuthorizationServerSecurityModule extends AbstractModule {

  private String url = "";

  public OauthAuthorizationServerSecurityModule(String url) {
    this.url = url;
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
    return new SecuredResources(Sets.newHashSet("/authorize"));
  }

  @Provides
  @UriPath
  public String uriPath() {
    return url;
  }
}
