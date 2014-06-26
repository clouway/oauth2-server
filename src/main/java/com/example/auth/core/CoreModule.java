package com.example.auth.core;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class CoreModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Clock.class).to(SystemClock.class).in(Singleton.class);
    bind(TokenGenerator.class).to(BearerTokenGenerator.class).in(Singleton.class);
    bind(TokenSecurity.class).to(TokenSecurityImpl.class).in(Singleton.class);
  }
}