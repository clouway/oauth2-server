package com.example.auth.core;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenSecurityTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ResourceOwnerRepository resourceOwners = context.mock(ResourceOwnerRepository.class);
  private TokenRepository tokens = context.mock(TokenRepository.class);

  private TokenSecurity generator = new TokenSecurityImpl(resourceOwners, tokens);

  @Test
  public void happyPath() throws Exception {
    final TokenRequest request = new TokenRequest("grant_type", "FeNoMeNa", "password");
    final Token token = new Token("generated_token", "Bearer");

    context.checking(new Expectations() {{
      oneOf(resourceOwners).exist(request.username, request.password);
      will(returnValue(true));

      oneOf(tokens).create();
      will(returnValue(token));
    }});

    Token actualToken = generator.create(request);

    assertThat(actualToken, is(token));
  }

  @Test(expected = ErrorResponseException.class)
  public void notMatchingCredentials() throws Exception {
    final TokenRequest request = new TokenRequest("type_grant", "Ivan", "123456");

    context.checking(new Expectations() {{
      oneOf(resourceOwners).exist(request.username, request.password);
      will(returnValue(false));
    }});

    generator.create(request);
  }
}