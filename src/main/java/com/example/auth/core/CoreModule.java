package com.example.auth.core;

import com.example.auth.core.authorization.AuthorizationSecurity;
import com.example.auth.core.authorization.AuthorizationSecurityImpl;
import com.example.auth.core.token.Sha1TokenGenerator;
import com.example.auth.core.token.TokenFactory;
import com.example.auth.core.token.TokenFactoryImpl;
import com.example.auth.core.token.TokenGenerator;
import com.example.auth.core.token.TokenSecurity;
import com.example.auth.core.token.TokenSecurityImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class CoreModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Clock.class).to(SystemClock.class).in(Singleton.class);
    bind(AccessTokenGenerator.class).to(BearerAccessTokenGenerator.class).in(Singleton.class);
    bind(TokenGenerator.class).to(Sha1TokenGenerator.class).in(Singleton.class);
    bind(TokenSecurity.class).to(TokenSecurityImpl.class).in(Singleton.class);
    bind(AuthorizationSecurity.class).to(AuthorizationSecurityImpl.class).in(Singleton.class);
  }

  @Provides
  public TokenFactory getTokenFactory(AccessTokenGenerator generator) {
    return new TokenFactoryImpl(generator,new SystemClock(),new Interval(50000));
  }
}