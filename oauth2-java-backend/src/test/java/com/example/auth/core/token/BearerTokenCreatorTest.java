package com.example.auth.core.token;

import com.example.auth.ArgumentCaptor;
import com.example.auth.core.Clock;
import com.example.auth.core.Duration;
import com.example.auth.core.authorization.Authorization;
import com.example.auth.core.authorization.ClientAuthorizationRepository;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static com.example.auth.core.Duration.minutes;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BearerTokenCreatorTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  private TokenCreator tokenCreator;

  private Clock clock = context.mock(Clock.class);

  private Duration duration = minutes(60);
  @Mock
  private TokenGenerator tokenGenerator;
  @Mock
  private TokenRepository repository;
  private String authCode = "sdfsdas 324 2342eas";
  @Mock
  private ClientAuthorizationRepository authorizationRepository;
  @Mock
  private TokenSecurity tokenSecurity;
  @Mock
  private RefreshTokenGenerator refreshTokenGenerator;

  private String clientId = "clientId";
  private String clientSecret = "clientSecret";


  @Before
  public void setUp() throws Exception {
    tokenCreator = new BearerTokenCreator(repository, tokenGenerator, tokenSecurity, authorizationRepository, clock, refreshTokenGenerator, duration);
  }

  @Test
  public void createByProvidedAuthCode() throws Exception {

    final ProvidedAuthorizationCode providedAuthorizationCode = new ProvidedAuthorizationCode(authCode, clientId, clientSecret);

    final String value = "1014bdf32c0edb3ef6e39a5ac551350f";
    final Date creationDate = new Date();

    final Authorization authorization = new Authorization("type", "clientId", authCode, "uri", "userID");

    final ArgumentCaptor<Token> savedToken = new ArgumentCaptor<Token>();
    context.checking(new Expectations() {{

      oneOf(tokenSecurity).validate(providedAuthorizationCode);

      oneOf(authorizationRepository).findByCode(authCode);
      will(returnValue(Optional.of(authorization)));

      oneOf(clock).now();
      will(returnValue(creationDate));

      oneOf(tokenGenerator).generate();
      will(returnValue(value));

      oneOf(refreshTokenGenerator).generate("");
      will(returnValue("refreshTokenValue"));

      oneOf(repository).save(with(savedToken));
    }});

    Token actualToken = tokenCreator.create(providedAuthorizationCode);

    assertThat(actualToken.value, is(equalTo(value)));
    assertThat(actualToken.type, is(equalTo("bearer")));
    assertThat(actualToken.expiresInSeconds, is(equalTo(duration.seconds)));
    assertThat(actualToken.creationDate, is(equalTo(creationDate)));
    assertThat(actualToken.userId, is(equalTo("userID")));
    assertThat(actualToken.refreshToken, is(equalTo("refreshTokenValue")));
    assertThat(actualToken, is(equalTo(savedToken.getValue())));
  }


  @Test
  public void emptyUserIdWhenAuthorizationNotFound() throws Exception {

    final ProvidedAuthorizationCode providedAuthorizationCode = new ProvidedAuthorizationCode(authCode, clientId, clientSecret);

    final String value = "1014bdf32c0edb3ef6e39a5ac551350f";
    final Date creationDate = new Date();

    final ArgumentCaptor<Token> savedToken = new ArgumentCaptor<Token>();
    context.checking(new Expectations() {{

      oneOf(tokenSecurity).validate(providedAuthorizationCode);

      oneOf(authorizationRepository).findByCode(authCode);
      will(returnValue(Optional.absent()));

      oneOf(clock).now();
      will(returnValue(creationDate));

      oneOf(tokenGenerator).generate();
      will(returnValue(value));

      oneOf(refreshTokenGenerator).generate("");
      will(returnValue("refreshTokenValue"));

      oneOf(repository).save(with(savedToken));
    }});

    Token actualToken = tokenCreator.create(providedAuthorizationCode);

    assertThat(actualToken.userId, is(equalTo("")));
  }

  @Test
  public void createByProvidedRefreshToken() throws Exception {

    final ProvidedRefreshToken providedRefreshToken = new ProvidedRefreshToken("old refreshToken", clientId, clientSecret);

    final String value = "1014bdf32c0edb3ef6e39a5ac551350f";

    final Date creationDate = new Date();

    final Token existingToken = new Token("", "", "", "userID", 0l, null);

    final ArgumentCaptor<Token> savedToken = new ArgumentCaptor<Token>();

    context.checking(new Expectations() {{

      oneOf(tokenSecurity).authenticateClient(clientId, clientSecret);

      oneOf(repository).findByRefreshTokenCode(providedRefreshToken.value);
      will(returnValue(Optional.of(existingToken)));

      oneOf(clock).now();
      will(returnValue(creationDate));

      oneOf(tokenGenerator).generate();
      will(returnValue(value));

      oneOf(refreshTokenGenerator).generate("old refreshToken");
      will(returnValue("refreshTokenValue"));

      oneOf(repository).save(with(savedToken));
    }});

    Token actualToken = tokenCreator.create(providedRefreshToken);

    assertThat(actualToken.value, is(equalTo(value)));
    assertThat(actualToken.type, is(equalTo("bearer")));
    assertThat(actualToken.expiresInSeconds, is(equalTo(duration.seconds)));
    assertThat(actualToken.creationDate, is(equalTo(creationDate)));
    assertThat(actualToken.userId, is(equalTo("userID")));
    assertThat(actualToken.refreshToken, is(equalTo("refreshTokenValue")));
    assertThat(actualToken, is(equalTo(savedToken.getValue())));
  }

  @Test(expected = TokenErrorResponse.class)
  public void tokenNotFoundForTheProvidedRefreshToken() throws Exception {

    final ProvidedRefreshToken providedRefreshToken = new ProvidedRefreshToken("old refreshToken", clientId, clientSecret);

    context.checking(new Expectations() {{

      oneOf(repository).findByRefreshTokenCode(providedRefreshToken.value);
      will(returnValue(Optional.absent()));

    }});

    Token actualToken = tokenCreator.create(providedRefreshToken);

  }
}