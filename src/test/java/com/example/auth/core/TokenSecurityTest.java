package com.example.auth.core;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenSecurityTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientAuthentication clientAuthentication = context.mock(ClientAuthentication.class);
  private AuthorizationVerifier authorizationVerifier = context.mock(AuthorizationVerifier.class);
  private TokenCreator tokenCreator = context.mock(TokenCreator.class);

  private TokenSecurity tokenSecurity = new TokenSecurityImpl(clientAuthentication, authorizationVerifier, tokenCreator);

  @Test
  public void happyPath() throws Exception {
    final TokenRequest request = new TokenRequest("grant_type", "code", "client_id", "client_secret");
    final Token token = new Token("generated_token", "Bearer");

    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(request.clientId, request.clientSecret);
      will(returnValue(true));

      oneOf(authorizationVerifier).verify(request.code, request.clientId);
      will(returnValue(true));

      oneOf(tokenCreator).create();
      will(returnValue(token));
    }});

    Token actualToken = tokenSecurity.create(request);

    assertThat(actualToken, is(token));
  }

  @Test(expected = TokenErrorResponse.class)
  public void wrongClientCredentials() throws Exception {
    final TokenRequest request = new TokenRequest("grant_type1", "code1", "client_id1", "client_secret1");

    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(request.clientId, request.clientSecret);
      will(returnValue(false));
    }});

    tokenSecurity.create(request);
  }

  @Test(expected = TokenErrorResponse.class)
  public void notExistingCode() throws Exception {
    final TokenRequest request = new TokenRequest("grant_type2", "code2", "client_id2", "client_secret2");

    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(request.clientId, request.clientSecret);
      will(returnValue(true));

      oneOf(authorizationVerifier).verify(request.code, request.clientId);
      will(returnValue(false));
    }});

    tokenSecurity.create(request);
  }
}