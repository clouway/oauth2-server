package com.clouway.oauth2;

import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.oauth2.client.JwtKeyStore;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.IdentityFinder;
import com.clouway.oauth2.user.ResourceOwnerIdentityFinder;

/**
 * OAuth2Config is a configuration class which is used to pass configuration from apps to the oauth2-server flow.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com) 
 * @see {@link OAuth2Servlet#config()}
 */
public final class OAuth2Config {


  public static OAuth2Config.Builder newConfig() {
    return new OAuth2Config.Builder();
  }

  public static class Builder {

    private Tokens tokens;
    private IdentityFinder identityFinder;
    private JwtKeyStore jwtKeyStore;
    private ClientAuthorizationRepository clientAuthorizationRepository;
    private ClientFinder clientFinder;
    private String loginPageUrl;
    private ResourceOwnerIdentityFinder resourceOwnerIdentityFinder;
    private KeyStore keyStore;

    public Builder tokens(Tokens tokens) {
      this.tokens = tokens;
      return this;
    }

    public Builder identityFinder(IdentityFinder identityFinder) {
      this.identityFinder = identityFinder;
      return this;
    }

    public Builder resourceOwnerIdentityFinder(ResourceOwnerIdentityFinder resourceOwnerIdentityFinder) {
      this.resourceOwnerIdentityFinder = resourceOwnerIdentityFinder;
      return this;
    }

    public Builder jwtKeyStore(JwtKeyStore jwtKeyStore) {
      this.jwtKeyStore = jwtKeyStore;
      return this;
    }

    public Builder clientAuthorizationRepository(ClientAuthorizationRepository clientAuthorizationRepository) {
      this.clientAuthorizationRepository = clientAuthorizationRepository;
      return this;
    }

    public Builder clientFinder(ClientFinder clientFinder) {
      this.clientFinder = clientFinder;
      return this;
    }

    public Builder loginPageUrl(String url) {
      this.loginPageUrl = url;
      return this;
    }

    public Builder keyStore(KeyStore keyStore) {
      this.keyStore = keyStore;
      return this;
    }

    public OAuth2Config build() {
      return new OAuth2Config(this);
    }

  }

  private final IdentityFinder identityFinder;
  private final ResourceOwnerIdentityFinder resourceOwnerIdentityFinder;
  private final ClientFinder clientFinder;

  private final Tokens tokens;
  private final ClientAuthorizationRepository clientAuthorizationRepository;
  private final JwtKeyStore jwtKeyStore;
  private final String loginPageUrl;
  private final KeyStore keyStore;

  private OAuth2Config(Builder builder) {
    this.tokens = builder.tokens;
    this.identityFinder = builder.identityFinder;
    this.resourceOwnerIdentityFinder = builder.resourceOwnerIdentityFinder;
    this.clientFinder = builder.clientFinder;
    this.clientAuthorizationRepository = builder.clientAuthorizationRepository;
    this.jwtKeyStore = builder.jwtKeyStore;
    this.loginPageUrl = builder.loginPageUrl;
    this.keyStore = builder.keyStore;
  }

  public ClientAuthorizationRepository clientAuthorizationRepository() {
    return this.clientAuthorizationRepository;
  }

  public JwtKeyStore jwtKeyStore() {
    return this.jwtKeyStore;
  }

  public Tokens tokens() {
    return this.tokens;
  }

  public IdentityFinder identityFinder() {
    return this.identityFinder;
  }

  public ClientFinder clientFinder() {
    return this.clientFinder;
  }

  public ResourceOwnerIdentityFinder resourceOwnerIdentityFinder() {
    return this.resourceOwnerIdentityFinder;
  }

  public String loginPageUrl() {
    return this.loginPageUrl;
  }

  public KeyStore keyStore() {
    return this.keyStore;
  }

}
