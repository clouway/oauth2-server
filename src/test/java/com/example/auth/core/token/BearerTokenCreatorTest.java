package com.example.auth.core.token;

import com.example.auth.ArgumentCaptor;
import com.example.auth.core.Clock;
import com.example.auth.core.Duration;
import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static com.example.auth.core.Duration.minutes;
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


  @Before
  public void setUp() throws Exception {
    tokenCreator = new BearerTokenCreator(repository, tokenGenerator, clock, duration);
  }

  @Test
  public void create() throws Exception {

    final String value = "1014bdf32c0edb3ef6e39a5ac551350f";
    final Date creationDate = new Date();

    final ArgumentCaptor<Token> tokenCaptor = new ArgumentCaptor<Token>();
    context.checking(new Expectations() {{
      oneOf(clock).now();
      will(returnValue(creationDate));

      oneOf(tokenGenerator).generate();
      will(returnValue(value));

      oneOf(repository).save(with(tokenCaptor));
    }});

    Token actualToken = tokenCreator.create();

    assertThat(actualToken.value, CoreMatchers.is(CoreMatchers.equalTo(value)));
    assertThat(actualToken.type, CoreMatchers.is(CoreMatchers.equalTo("bearer")));
    assertThat(actualToken.expiresInSeconds, CoreMatchers.is(CoreMatchers.equalTo(duration.seconds)));
    assertThat(actualToken.creationDate, CoreMatchers.is(CoreMatchers.equalTo(creationDate)));
    assertThat(actualToken, CoreMatchers.is(CoreMatchers.equalTo(tokenCaptor.getValue())));
  }
}