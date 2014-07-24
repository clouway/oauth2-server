package com.example.auth.memory;

import com.example.auth.core.ResourceOwnerAuthentication;
import com.example.auth.core.ResourceOwnerStore;
import com.example.auth.core.SessionSecurity;
import com.example.auth.core.authorization.ClientAuthorizationRepository;
import com.example.auth.core.client.ClientAuthentication;
import com.example.auth.core.client.ClientRepository;
import com.example.auth.core.token.Sha1TokenGenerator;
import com.example.auth.core.token.TokenRepository;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class OauthAuthorizationServerInMemoryModule extends AbstractModule {
  @Override
  protected void configure() {

    InMemoryResourceOwnerRepository resourceOwnerRepository = new InMemoryResourceOwnerRepository(new Sha1TokenGenerator());

    bind(ResourceOwnerStore.class).toInstance(resourceOwnerRepository);
    bind(ResourceOwnerAuthentication.class).toInstance(resourceOwnerRepository);
    bind(SessionSecurity.class).toInstance(resourceOwnerRepository);

    InMemoryClientRepository clientRepository = new InMemoryClientRepository();
    bind(ClientRepository.class).toInstance(clientRepository);
    bind(ClientAuthentication.class).toInstance(clientRepository);

    InMemoryClientAuthorizationRepository authorizationRepository = new InMemoryClientAuthorizationRepository();
    bind(ClientAuthorizationRepository.class).toInstance(authorizationRepository);

  }


  @Provides
  TokenRepository getTokenRepository() {
    return new InMemoryTokenRepository(new Date());
  }

}