package com.example.auth.core.token.refreshtoken;

import com.example.auth.core.Clock;
import com.example.auth.core.token.TokenGenerator;
import com.google.inject.Provider;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RefreshTokenProviderImplTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  private final Date currentDate = new Date();

  @Mock
  RefreshTokenRepository refreshTokenRepository;

  @Mock
  Clock clock;

  private RefreshTokenProvider provider;
  private String secret = "the SECRET";
  private String clientId = "clientId";
  private String existingToken = "oldRefreshToken";
  private Boolean generateNewRefreshToken  = true;
  private Provider<Boolean> generateNewRefreshTokenProvider  = new Provider<Boolean>() {
    @Override
    public Boolean get() {
      return generateNewRefreshToken;
    }
  };

  @Mock
  private TokenGenerator tokenGenerator;

  @Before
  public void setUp() throws Exception {
    provider = new RefreshTokenProviderImpl(refreshTokenRepository, clock, generateNewRefreshTokenProvider, tokenGenerator);
  }

  @Test
  public void provideNewlyGeneratedToken() throws Exception {
    //configuration
    generateNewRefreshToken = true;

    final String generatedToken = "12312 fgdfklg a  fk2334234";
    final RefreshToken refreshToken = new RefreshToken(generatedToken, clientId, secret);

    context.checking(new Expectations() {{

      oneOf(refreshTokenRepository).delete(existingToken);

      oneOf(tokenGenerator).generate();
      will(returnValue(generatedToken));

      oneOf(clock).now();
      will(returnValue(currentDate));

      oneOf(refreshTokenRepository).save(refreshToken);
    }});

    RefreshToken token = provider.provide(existingToken, clientId, secret);

    assertThat(token,is(equalTo(refreshToken)));
  }


  @Test
  public void shouldNotDeleteTheOldTokenWhenNoOldRefreshTokenProvided() throws Exception {
    //configuration
    generateNewRefreshToken = true;
    existingToken = "";


    final String generatedToken = "12312 fgdfklg a  fk2334234";
    final RefreshToken refreshToken = new RefreshToken(generatedToken, clientId, secret);

    context.checking(new Expectations() {{

      oneOf(tokenGenerator).generate();
      will(returnValue(generatedToken));

      oneOf(clock).now();
      will(returnValue(currentDate));

      oneOf(refreshTokenRepository).save(refreshToken);
    }});

    RefreshToken token = provider.provide(existingToken, clientId, secret);

    assertThat(token,is(equalTo(refreshToken)));
  }


  @Test
  public void shouldNotCreateNewToken() throws Exception {
    //configuration
    generateNewRefreshToken = false;

    final RefreshToken expectedToken = new RefreshToken(existingToken, clientId, secret);

    RefreshToken token = provider.provide(existingToken, clientId, secret);

    assertThat(token,is(equalTo(expectedToken)));
  }


  @Test
  public void generateNewTokenWhenNoProvidedExisting() throws Exception {
    //configuration
    generateNewRefreshToken = false;
    existingToken = "";


    final String generatedToken = "12312 fgdfklg a  fk2334234";
    final RefreshToken expectedToken = new RefreshToken(generatedToken, clientId, secret);

    context.checking(new Expectations() {{

      oneOf(tokenGenerator).generate();
      will(returnValue(generatedToken));

      oneOf(refreshTokenRepository).save(expectedToken);

    }});

    RefreshToken token = provider.provide(existingToken, clientId, secret);

    assertThat(token,is(equalTo(expectedToken)));
  }
}