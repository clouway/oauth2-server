package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.Duration;
import com.clouway.oauth2.OAuth2Config;
import com.clouway.oauth2.OAuth2Servlet;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.client.ServiceAccountFinder;
import com.clouway.oauth2.exampleapp.security.SecurityModule;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.IdentityFinder;
import com.clouway.oauth2.user.ResourceOwnerIdentityFinder;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.google.sitebricks.SitebricksModule;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class OauthAuthorizationServerModule extends AbstractModule {

  @Singleton
  static class OAuth2ServletBinding extends OAuth2Servlet {

    private final ClientAuthorizationRepository clientAuthorizationRepository;
    private final ClientRepository clientRepository;
    private final Tokens tokens;
    private final IdentityFinder identityFinder;
    private final ResourceOwnerIdentityFinder resourceOwnerIdentityFinder;
    private final ServiceAccountFinder serviceAccountFinder;

    @Inject
    public OAuth2ServletBinding(ClientAuthorizationRepository clientAuthorizationRepository, ClientRepository clientRepository, Tokens tokens, IdentityFinder identityFinder, ResourceOwnerIdentityFinder resourceOwnerIdentityFinder, ServiceAccountFinder serviceAccountFinder) {
      this.clientAuthorizationRepository = clientAuthorizationRepository;
      this.clientRepository = clientRepository;
      this.tokens = tokens;
      this.identityFinder = identityFinder;
      this.resourceOwnerIdentityFinder = resourceOwnerIdentityFinder;
      this.serviceAccountFinder = serviceAccountFinder;
    }

    @Override
    protected OAuth2Config config() {
      return OAuth2Config.newConfig()
              .loginPageUrl("/r/oauth/login?continue=")
              .clientAuthorizationRepository(clientAuthorizationRepository)
              .clientRepository(clientRepository)
              .tokens(tokens)
              .identityFinder(identityFinder)
              .resourceOwnerIdentityFinder(resourceOwnerIdentityFinder)
              .serviceAccountRepository(serviceAccountFinder)
              .build();
    }
  }


  private String url = "";
  private String loginPagePath = "";
  private Duration tokenTimeToLive;
  private Boolean generatesNewRefreshToken = false;

  /**
   * constructs the module
   *
   * @param url
   * @param tokenTimeToLive in seconds
   */
  public OauthAuthorizationServerModule(String url, Long tokenTimeToLive) {
    this.url = url;
    this.tokenTimeToLive = Duration.seconds(tokenTimeToLive);
  }

  public OauthAuthorizationServerModule(String url, String loginPagePath, Long tokenTimeToLive) {
    this.url = url;
    this.loginPagePath = loginPagePath;
    this.tokenTimeToLive = Duration.seconds(tokenTimeToLive);
  }

  /**
   * constructs the module
   *
   * @param url
   * @param tokenTimeToLive in seconds
   */
  public OauthAuthorizationServerModule(String url, Long tokenTimeToLive, Boolean generatesNewRefreshToken) {
    this.url = url;
    this.generatesNewRefreshToken = generatesNewRefreshToken;
    this.tokenTimeToLive = Duration.seconds(tokenTimeToLive);
  }

  /**
   * constructs the module
   *
   * @param url
   * @param tokenTimeToLive in seconds
   */
  public OauthAuthorizationServerModule(String url, String loginPagePath, Long tokenTimeToLive, Boolean generatesNewRefreshToken) {
    this.url = url;
    this.loginPagePath = loginPagePath;
    this.generatesNewRefreshToken = generatesNewRefreshToken;
    this.tokenTimeToLive = Duration.seconds(tokenTimeToLive);
  }


  protected void configure() {
    install(new SecurityModule(url, loginPagePath));
    install(new ServletModule() {
      @Override
      protected void configureServlets() {
        serve("/oauth2/*").with(OAuth2ServletBinding.class);
        serve("/testapi").with(new HttpServlet() {
          @Override
          protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            String token = req.getHeader("Authorization").split("\\s")[0];

            PrintWriter writer = resp.getWriter();
            writer.println("Hello !!!");
            writer.flush();
          }
        });
      }
    });

    install(new SitebricksModule() {
      @Override
      protected void configureSitebricks() {
        at(url + "/login").show(LoginEndpoint.class);
        at(url + "/userInfo/:token").serve(UserInfoEndPoint.class);
      }
    });

  }


  @Provides
  @TokenTimeToLive
  public Duration getTokenLive() {
    return tokenTimeToLive;
  }

  @Provides
  @GenerateNewRefreshToken
  public Boolean getGeneratesNewRefreshToken() {
    return generatesNewRefreshToken;
  }
}
