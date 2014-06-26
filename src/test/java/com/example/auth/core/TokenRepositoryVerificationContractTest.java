package com.example.auth.core;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static com.example.auth.core.Interval.minutes;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public abstract class TokenRepositoryVerificationContractTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private TokenGenerator tokenGenerator = context.mock(TokenGenerator.class);
  private Clock clock = context.mock(Clock.class);
  private Interval interval = minutes(60);

  private TokenRepository repository = create(tokenGenerator, clock, interval);

  @Test
  public void happyPath() throws Exception {
    final Token token = new Token("9c5084d190264d0de737a8049ed630fd", "bearer");
    final Date expirationTime = new Date(System.currentTimeMillis() + 9000000);

    create(token, expirationTime);

    context.checking(new Expectations() {{
      oneOf(clock).now();
      will(returnValue(new Date()));

      ignoring(clock).nowPlus(interval);
    }});

    Boolean exists = repository.verify("9c5084d190264d0de737a8049ed630fd");

    assertTrue(exists);
  }

  @Test
  public void updateExpirationTime() throws Exception {
    final Token token = new Token("9c5084d190264d0de737a8049ed630fd", "bearer");
    final Date expirationTime = new Date(System.currentTimeMillis() + 10000000);
    final Date updatedTime = new Date(System.currentTimeMillis() + 30000000);

    create(token, expirationTime);

    context.checking(new Expectations() {{
      oneOf(clock).now();
      will(returnValue(new Date()));

      oneOf(clock).now();
      will(returnValue(new Date(System.currentTimeMillis() + 20000000)));

      exactly(2).of(clock).nowPlus(interval);
      will(returnValue(updatedTime));
    }});

    assertTrue(repository.verify("9c5084d190264d0de737a8049ed630fd"));
    assertTrue(repository.verify("9c5084d190264d0de737a8049ed630fd"));
  }

  @Test
  public void notExistingToken() throws Exception {
    Boolean exists = repository.verify("1265fff34bdc4d976b02196845edb967");

    assertFalse(exists);
  }

  @Test
  public void expiredToken() throws Exception {
    final Token token = new Token("1014bdf32c0edb3ef6e39a5ac551350f", "bearer");
    final Date expirationDate = new Date(System.currentTimeMillis() - 9000000);

    create(token, expirationDate);

    context.checking(new Expectations() {{
      oneOf(clock).now();
      will(returnValue(new Date()));
    }});

    Boolean exists = repository.verify("1014bdf32c0edb3ef6e39a5ac551350f");

    assertFalse(exists);
  }

  private void create(final Token token, final Date expirationDate) {
    context.checking(new Expectations() {{
      oneOf(clock).nowPlus(interval);
      will(returnValue(expirationDate));

      oneOf(tokenGenerator).generate();
      will(returnValue(token));
    }});

    repository.create();
  }

  protected abstract TokenRepository create(TokenGenerator tokenGenerator, Clock clock, Interval interval);
}