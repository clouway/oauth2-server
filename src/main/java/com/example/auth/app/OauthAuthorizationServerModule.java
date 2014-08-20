/*
 * Created by IntelliJ IDEA.
 * User: mlesikov
 * Date: 7/22/14
 * Time: 2:00 PM
 */
package com.example.auth.app;

import com.example.auth.app.security.SecurityModule;
import com.example.auth.core.CoreModule;
import com.example.auth.core.Duration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class OauthAuthorizationServerModule extends AbstractModule {

  private String url = "";
  private Duration tokenTimeToLive;

  /**
   * constructs the module
   * @param url
   * @param tokenTimeToLive in seconds
   */
  public OauthAuthorizationServerModule(String url, Long tokenTimeToLive) {
    this.url = url;
    this.tokenTimeToLive = Duration.seconds(tokenTimeToLive);
  }


  protected void configure() {

    install(new CoreModule());
    install(new SecurityModule(url));
//    install(new SitebricksModule() {
//      @Override
//      protected void configureSitebricks() {
//        at(url + "/register").serve(RegistrationEndpoint.class);
//        at(url + "/authorize").serve(AuthorizationEndpoint.class);
//        at(url + "/login").show(LoginEndpoint.class);
//        at(url + "/token").serve(TokenEndpoint.class);
//        at(url + "/verify").serve(VerificationEndpoint.class);
//      }
//    });
  }


  @Provides
  @TokenTimeToLive
  public Duration getTokenLive() {
    return tokenTimeToLive;
  }
}
