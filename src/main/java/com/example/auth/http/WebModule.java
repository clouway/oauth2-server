package com.example.auth.http;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.google.sitebricks.SitebricksModule;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class WebModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new ServletModule() {
      @Override
      protected void configureServlets() {
        filter("/*").through(SecurityFilter.class);
      }
    });

    install(new SitebricksModule() {
      @Override
      protected void configureSitebricks() {
        at("/register").serve(RegistrationEndpoint.class);
        at("/authorize").serve(AuthorizationEndpoint.class);
        at("/login").show(LoginEndpoint.class);
        at("/token").serve(TokenEndpoint.class);
        at("/verify").serve(VerificationEndpoint.class);
      }
    });
  }

  @Provides
  @Singleton
  public SecuredResources provideSecuredResources() {
    return new SecuredResources() {{
      add("/authorize");
    }};
  }
}