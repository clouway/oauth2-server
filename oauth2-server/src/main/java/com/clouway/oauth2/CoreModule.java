package com.clouway.oauth2;

import com.clouway.oauth2.token.Sha1TokenGenerator;
import com.clouway.oauth2.token.TokenGenerator;
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
    bind(TokenGenerator.class).to(Sha1TokenGenerator.class).in(Singleton.class);
  }

  @Provides
  public boolean getGenerateNewRefreshToken() {
    return true;
  }
}