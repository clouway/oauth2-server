package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.OAuth2Config;
import com.clouway.oauth2.OAuth2Servlet;
import com.clouway.oauth2.ResourceOwnerIdentityFinder;
import com.clouway.oauth2.authorization.ClientAuthorizer;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.oauth2.client.JwtKeyStore;
import com.clouway.oauth2.common.Duration;
import com.clouway.oauth2.exampleapp.security.SecurityModule;
import com.clouway.oauth2.keystore.IdentityKeyPair;
import com.clouway.oauth2.keystore.KeyStore;
import com.clouway.oauth2.token.IdentityFinder;
import com.clouway.oauth2.token.Tokens;

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
import java.util.Collections;
import java.util.List;

public class OAuthAuthorizationServerModule extends AbstractModule {

  @Singleton
  static class OAuth2ServletBinding extends OAuth2Servlet {

    private final ClientAuthorizer clientAuthorizer;
    private final ClientFinder clientFinder;
    private final Tokens tokens;
    private final IdentityFinder identityFinder;
    private final ResourceOwnerIdentityFinder resourceOwnerIdentityFinder;
    private final JwtKeyStore jwtKeyStore;

    @Inject
    public OAuth2ServletBinding(ClientAuthorizer clientAuthorizer, ClientFinder clientFinder, Tokens tokens, IdentityFinder identityFinder, ResourceOwnerIdentityFinder resourceOwnerIdentityFinder, JwtKeyStore jwtKeyStore) {
      this.clientAuthorizer = clientAuthorizer;
      this.clientFinder = clientFinder;
      this.tokens = tokens;
      this.identityFinder = identityFinder;
      this.resourceOwnerIdentityFinder = resourceOwnerIdentityFinder;
      this.jwtKeyStore = jwtKeyStore;
    }

    @Override
    protected OAuth2Config config() {
      return OAuth2Config.newConfig()
              .loginPageUrl("/r/oauth/login?continue=")
              .clientAuthorizationRepository(clientAuthorizer)
              .clientFinder(clientFinder)
              .tokens(tokens)
              .identityFinder(identityFinder)
              .resourceOwnerIdentityFinder(resourceOwnerIdentityFinder)
              .jwtKeyStore(jwtKeyStore)
              .keyStore(new KeyStore() {
                @Override
                public List<IdentityKeyPair> getKeys() {
                  return Collections.emptyList();
                }
              })
              .build();
    }
  }

  private String loginPagePath = "";
  private Duration tokenTimeToLive;
  private Boolean generatesNewRefreshToken = false;

  public OAuthAuthorizationServerModule(String loginPagePath, Long tokenTimeToLive) {
    this.loginPagePath = loginPagePath;
    this.tokenTimeToLive = Duration.seconds(tokenTimeToLive);
  }

  /**
   * constructs the module
   *
   * @param tokenTimeToLive in seconds
   */
  public OAuthAuthorizationServerModule(String loginPagePath, Long tokenTimeToLive, Boolean generatesNewRefreshToken) {
    this.loginPagePath = loginPagePath;
    this.generatesNewRefreshToken = generatesNewRefreshToken;
    this.tokenTimeToLive = Duration.seconds(tokenTimeToLive);
  }

  protected void configure() {
    install(new SecurityModule("/r/oauth", loginPagePath));
    install(new ServletModule() {
      @Override
      protected void configureServlets() {
        serve("/oauth2/*").with(OAuth2ServletBinding.class);
        serve("/testapi").with(new HttpServlet() {
          @Override
          protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        at("/r/oauth/login").show(LoginEndpoint.class);
        at("/r/oauth/userInfo/:token").serve(UserInfoEndPoint.class);
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
