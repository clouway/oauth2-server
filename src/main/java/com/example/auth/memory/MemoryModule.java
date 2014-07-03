package com.example.auth.memory;

import com.example.auth.core.*;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import static com.example.auth.core.Interval.hours;
import static com.example.auth.core.Interval.minutes;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class MemoryModule extends AbstractModule {
  @Override
  protected void configure() {
    InMemoryResourceOwnerRepository resourceOwnerRepository = new InMemoryResourceOwnerRepository(new Sha1TokenGenerator());
    bind(ResourceOwnerStore.class).toInstance(resourceOwnerRepository);
    bind(ResourceOwnerAuthentication.class).toInstance(resourceOwnerRepository);
    bind(SessionSecurity.class).toInstance(resourceOwnerRepository);

    InMemoryClientRepository clientRepository = new InMemoryClientRepository(new Sha1TokenGenerator());
    bind(ClientRegister.class).toInstance(clientRepository);
    bind(ClientFinder.class).toInstance(clientRepository);
    bind(ClientAuthentication.class).toInstance(clientRepository);

    InMemoryAuthorizationRepository authorizationRepository = new InMemoryAuthorizationRepository();
    bind(AuthorizationStore.class).toInstance(authorizationRepository);
    bind(AuthorizationVerifier.class).toInstance(authorizationRepository);

    InMemoryTokenRepository tokenRepository = new InMemoryTokenRepository(new BearerAccessTokenGenerator(), new SystemClock(), hours(24));
    bind(TokenCreator.class).toInstance(tokenRepository);
    bind(TokenVerifier.class).toInstance(tokenRepository);
  }
}