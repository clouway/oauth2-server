package com.example.auth.core.token;

import com.google.inject.Provider;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RefreshTokenGeneratorImplTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  private RefreshTokenGenerator generator;
  @Mock
  private TokenGenerator tokenGenerator;
  @Mock
  private Provider<Boolean> generateNewRefreshToken;

  @Before
  public void setUp() throws Exception {
    generator = new RefreshTokenGeneratorImpl(tokenGenerator, generateNewRefreshToken);

  }

  @Test
  public void generateEveryTimeNewTokenEventExistingIsPassed() throws Exception {

    context.checking(new Expectations() {{
      oneOf(generateNewRefreshToken).get();
      will(returnValue(true));
      oneOf(tokenGenerator).generate();
      will(returnValue("new token"));
    }});

    String generatedToken = generator.generate("existing");

    assertThat(generatedToken, is(equalTo("new token")));
  }


  @Test
  public void useExistingToken() throws Exception {

    context.checking(new Expectations() {{
      oneOf(generateNewRefreshToken).get();
      will(returnValue(false));
    }});

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