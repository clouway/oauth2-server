/*
 * Created by IntelliJ IDEA.
 * User: mlesikov
 * Date: 7/22/14
 * Time: 2:00 PM
 */
package com.example.auth.app;

import com.example.auth.core.CoreModule;
import com.example.auth.app.security.OauthAuthorizationServerSecurityModule;
import com.google.inject.AbstractModule;
import com.google.sitebricks.SitebricksModule;

public class OauthAuthorizationServerModule extends AbstractModule {

  private String url = "";

  public OauthAuthorizationServerModule(String url) {
    this.url = url;
  }


  protected void configure() {
    install(new CoreModule());
    install(new OauthAuthorizationServerSecurityModule(url));
    install(new SitebricksModule() {
      @Override
      protected void configureSitebricks() {
        at(url + "/register").serve(RegistrationEndpoint.class);
        at(url + "/authorize").serve(AuthorizationEndpoint.class);
        at(url + "/login").show(LoginEndpoint.class);
        at(url + "/token").serve(TokenEndpoint.class);
        at(url + "/verify").serve(VerificationEndpoint.class);
      }
    });
  }
}
