package com.example.auth.core.token;

import com.example.auth.core.authorization.ClientAuthorizationRequestVerifier;
import com.example.auth.core.client.ClientAuthentication;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
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

  private ClientAuthentication clientAuthentication = context.mock(ClientAuthentication.class);
  private ClientAuthorizationRequestVerifier clientAuthorizationRequestVerifier = context.mock(ClientAuthorizationRequestVerifier.class);
  @Mock
  private TokenRepository tokenRepository;

  @Mock
  private TokenFactory tokenFactory;

  private TokenSecurity tokenSecurity;

  @Before
  public void setUp() throws Exception {
    tokenSecurity = new TokenSecurityImpl(clientAuthentication, clientAuthorizationRequestVerifier, tokenRepository, tokenFactory);

  }

  @Test
  public void happyPath() throws Exception {
    final TokenRequest request = new TokenRequest("grant_type", "code", "client_id", "client_secret");
    final Token token = new Token("generated_token", "Bearer", null);

    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(request.clientId, request.clientSecret);
      will(returnValue(true));

      oneOf(clientAuthorizationRequestVerifier).verify(request.code, request.clientId);
      will(returnValue(true));

      oneOf(tokenFactory).create();
      will(returnValue(token));

      oneOf(tokenRepository).save(token);
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

      oneOf(clientAuthorizationRequestVerifier).verify(request.code, request.clientId);
      will(returnValue(false));
    }});

    tokenSecurity.create(request);
  }
}