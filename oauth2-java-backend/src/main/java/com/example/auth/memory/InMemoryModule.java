package com.example.auth.memory;

import com.clouway.oauth2.Duration;
import com.clouway.oauth2.ResourceOwnerAuthentication;
import com.clouway.oauth2.ResourceOwnerStore;
import com.clouway.oauth2.SessionSecurity;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.token.Sha1TokenGenerator;
import com.clouway.oauth2.token.TokenRepository;
import com.clouway.oauth2.user.UserIdFinder;
import com.clouway.oauth2.user.UserRepository;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryModule extends AbstractModule {
  @Override
  protected void configure() {

    InMemoryResourceOwnerRepository resourceOwnerRepository = new InMemoryResourceOwnerRepository(new Sha1TokenGenerator());

    bind(ResourceOwnerStore.class).toInstance(resourceOwnerRepository);
    bind(ResourceOwnerAuthentication.class).toInstance(resourceOwnerRepository);
    bind(SessionSecurity.class).toInstance(resourceOwnerRepository);

    InMemoryClientRepository clientRepository = new InMemoryClientRepository();
    bind(ClientRepository.class).toInstance(clientRepository);

    InMemoryClientAuthorizationRepository authorizationRepository = new InMemoryClientAuthorizationRepository();
    bind(ClientAuthorizationRepository.class).toInstance(authorizationRepository);

    bind(UserIdFinder.class).to(InMemoryUserRepository.class);
    bind(UserRepository.class).to(InMemoryUserRepository.class);

  }


  @Provides
  TokenRepository getTokenRepository() {
    return new InMemoryTokenRepository(new Date(), new Duration(900000000l));
  }

}