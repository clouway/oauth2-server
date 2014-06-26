package com.example.auth.memory;

import com.example.auth.core.Clock;
import com.example.auth.core.ResourceOwnerRepository;
import com.example.auth.core.TokenGenerator;
import com.example.auth.core.TokenRepository;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import static com.example.auth.core.Interval.minutes;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class MemoryModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ResourceOwnerRepository.class).to(InMemoryResourceOwnerRepository.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  public TokenRepository provideTokenRepository(TokenGenerator tokenGenerator, Clock clock) {
    return new InMemoryTokenRepository(tokenGenerator, clock, minutes(60));
  }
}