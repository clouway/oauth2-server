package com.example.auth.core.token;

import com.example.auth.core.authorization.TokenCreationVerifier;
import com.example.auth.core.client.ClientAuthentication;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class TokenSecurityImplTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ClientAuthentication clientAuthentication;

  @Mock
  private TokenCreationVerifier tokenCreationVerifier;

  private TokenSecurity tokenSecurity;

  @Before
  public void setUp() throws Exception {
    tokenSecurity = new TokenSecurityImpl(clientAuthentication, tokenCreationVerifier);
  }

  @Test
  public void validRefreshToken() throws Exception {
    final TokenRequest request = new TokenRequest("grant_type", "code", "refreshToken", "client_id", "client_secret");
    context.checking(new Expectations() {{

      oneOf(tokenCreationVerifier).verifyRefreshToken(request.clientId, request.clientSecret, request.refreshToken);
      will(returnValue(true));
    }});

    tokenSecurity.validateRefreshToken(request);
  }

  @Test(expected = TokenErrorResponse.class)
  public void notValidRefreshToken() throws Exception {
    final TokenRequest request = new TokenRequest("grant_type2", "refresh_value2", "code2", "client_id2", "client_secret2");

    context.checking(new Expectations() {{
      oneOf(tokenCreationVerifier).verifyRefreshToken(request.clientId, request.clientSecret, request.refreshToken);
      will(returnValue(false));
    }});

    tokenSecurity.validateRefreshToken(request);
  }

  @Test
  public void validAuthCode() throws Exception {
    final TokenRequest request = new TokenRequest("grant_type", "code", "refreshToken", "client_id", "client_secret");
    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(request.clientId, request.clientSecret);
      will(returnValue(true));

      oneOf(tokenCreationVerifier).verify(request.code, request.clientId);
      will(returnValue(true));
    }});

    tokenSecurity.validateAuthCode(request);

  }

  @Test(expected = TokenErrorResponse.class)
  public void wrongClientCredentials() throws Exception {
    final TokenRequest request = new TokenRequest("grant_type1", "refresh_value1", "code1", "client_id1", "client_secret1");

    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(request.clientId, request.clientSecret);
      will(returnValue(false));
    }});

    tokenSecurity.validateAuthCode(request);
  }

  @Test(expected = TokenErrorResponse.class)
  public void notExistingCode() throws Exception {
    final TokenRequest request = new TokenRequest("grant_type2", "refresh_value2", "code2", "client_id2", "client_secret2");

    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(request.clientId, request.clientSecret);
      will(returnValue(true));

      oneOf(tokenCreationVerifier).verify(request.code, request.clientId);
      will(returnValue(false));
    }});

    tokenSecurity.validateAuthCode(request);
  }
}