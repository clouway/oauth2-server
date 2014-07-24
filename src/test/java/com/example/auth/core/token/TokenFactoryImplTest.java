package com.example.auth.core.token;

import com.example.auth.core.AccessTokenGenerator;
import com.example.auth.core.Clock;
import com.example.auth.core.Interval;
import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static com.example.auth.core.Interval.minutes;
import static org.junit.Assert.assertThat;

public class TokenFactoryImplTest {
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  private TokenFactory tokenFactory;

  private Clock clock = context.mock(Clock.class);

  private Interval interval = minutes(60);

  @Mock
  private AccessTokenGenerator tokenGenerator;


  @Before
  public void setUp() throws Exception {
    tokenFactory = new TokenFactoryImpl(tokenGenerator, clock, interval);
  }

  @Test
  public void create() throws Exception {

    final Token token = new Token("1014bdf32c0edb3ef6e39a5ac551350f", "bearer");
    final Date expirationDate = new Date(System.currentTimeMillis() - 9000000);
    context.checking(new Expectations() {{
      oneOf(clock).nowPlus(interval);
      will(returnValue(expirationDate));

      oneOf(tokenGenerator).generate();
      will(returnValue(token));
    }});

    Token actualToken = tokenFactory.create();

    assertThat(actualToken, CoreMatchers.is(CoreMatchers.equalTo(token)));
  }
}