package com.clouway.oauth2.token;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RefreshTokenGeneratorImplTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  TokenGenerator tokenGenerator;

  private RefreshTokenGenerator generator;

  @Before
  public void setUp() throws Exception {
    generator = new RefreshTokenGeneratorImpl(tokenGenerator, true);

  }

  @Test
  public void generateEveryTimeNewTokenEventExistingIsPassed() throws Exception {
    context.checking(new Expectations() {{
      oneOf(tokenGenerator).generate();
      will(returnValue("new token"));
    }});

    String generatedToken = generator.generate("existing");

    assertThat(generatedToken, is(equalTo("new token")));
  }


  @Test
  public void useExistingToken() throws Exception {
    generator = new RefreshTokenGeneratorImpl(tokenGenerator, false);

    String generatedToken = generator.generate("existing");

    assertThat(generatedToken, is(equalTo("existing")));
  }

  @Test
  public void generateEveryTimeNewTokenWhenNoExistingToken() throws Exception {

    context.checking(new Expectations() {{
      oneOf(tokenGenerator).generate();
      will(returnValue("new token"));
    }});

    String generatedToken = generator.generate("");

    assertThat(generatedToken, is(equalTo("new token")));

  }

  @Test
  public void generateEveryTimeNewTokenWhenNullExistingToken() throws Exception {

    context.checking(new Expectations() {{
      oneOf(tokenGenerator).generate();
      will(returnValue("new token"));
    }});

    String generatedToken = generator.generate(null);

    assertThat(generatedToken, is(equalTo("new token")));

  }
}