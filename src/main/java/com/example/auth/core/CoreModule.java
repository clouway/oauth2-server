package com.example.auth.core;

import com.example.auth.core.authorization.AuthorizationSecurity;
import com.example.auth.core.authorization.AuthorizationSecurityImpl;
import com.example.auth.core.token.*;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import static com.example.auth.core.Interval.minutes;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class CoreModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Clock.class).to(SystemClock.class).in(Singleton.class);
    bind(TokenGenerator.class).to(Sha1TokenGenerator.class).in(Singleton.class);
    bind(TokenSecurity.class).to(TokenSecurityImpl.class).in(Singleton.class);
    bind(AuthorizationSecurity.class).to(AuthorizationSecurityImpl.class).in(Singleton.class);
  }

  @Provides
  public TokenFactory getTokenFactory(TokenGenerator generator) {
    return new BearerTokenFactoryImpl(generator, new SystemClock(), minutes(60));
  }
}