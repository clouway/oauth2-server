package com.example.auth.memory;

import com.example.auth.core.*;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.sitebricks.headless.Service;

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

    bind(ClientRepository.class).to(InMemoryClientRepository.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  public TokenRepository provideTokenRepository(AccessTokenGenerator tokenGenerator, Clock clock) {
    return new InMemoryTokenRepository(tokenGenerator, clock, minutes(60));
  }
}