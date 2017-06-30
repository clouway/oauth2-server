package com.clouway.oauth2;

import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.oauth2.client.ClientKeyStore;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.IdentityFinder;
import com.clouway.oauth2.user.ResourceOwnerIdentityFinder;

/**
 * OAuth2Config is a configuration class which is used to pass configuration from apps to the oauth2-server flow.
 *
 * @see {@link OAuth2Servlet#config()}
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class OAuth2Config {



  public static OAuth2Config.Builder newConfig() {
    return new OAuth2Config.Builder();
  }

  public static class Builder {

    private Tokens tokens;
    private IdentityFinder identityFinder;
    private ClientKeyStore clientKeyStore;
    private ClientAuthorizationRepository clientAuthorizationRepository;
    private ClientFinder clientFinder;
    private String loginPageUrl;
    private ResourceOwnerIdentityFinder resourceOwnerIdentityFinder;
    private PublicKeys publicKeys;

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

    public Builder serviceAccountRepository(ClientKeyStore clientKeyStore) {
      this.clientKeyStore = clientKeyStore;
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

    public Builder publicKeys(PublicKeys publicKeys) {
      this.publicKeys = publicKeys;
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
  private final ClientKeyStore clientKeyStore;
  private final String loginPageUrl;
  private final PublicKeys publicKeys;

  private OAuth2Config(Builder builder) {
    this.tokens = builder.tokens;
    this.identityFinder = builder.identityFinder;
    this.resourceOwnerIdentityFinder = builder.resourceOwnerIdentityFinder;
    this.clientFinder = builder.clientFinder;
    this.clientAuthorizationRepository = builder.clientAuthorizationRepository;
    this.clientKeyStore = builder.clientKeyStore;
    this.loginPageUrl = builder.loginPageUrl;
    this.publicKeys = builder.publicKeys;
  }

  public ClientAuthorizationRepository clientAuthorizationRepository() {
    return this.clientAuthorizationRepository;
  }

  public ClientKeyStore serviceAccountRepository() {
    return this.clientKeyStore;
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

  public PublicKeys publicKeys() {
    return this.publicKeys;
  }

}
