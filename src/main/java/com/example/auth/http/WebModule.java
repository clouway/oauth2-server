package com.example.auth.http;

import com.google.inject.AbstractModule;
import com.google.sitebricks.SitebricksModule;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class WebModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new SitebricksModule() {
      @Override
      protected void configureSitebricks() {
        at("/token").serve(TokenEndpoint.class);
        at("/verify").serve(VerificationEndpoint.class);
      }
    });
  }
}