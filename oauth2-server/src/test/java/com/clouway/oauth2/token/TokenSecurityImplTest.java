package com.clouway.oauth2.token;

import com.clouway.oauth2.authorization.TokenCreationVerifier;
import com.clouway.oauth2.client.ClientAuthentication;
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
  public void authenticateClient() throws Exception {
    final String clientId = "client_id";
    final String clientSecret = "client_secret";

    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(clientId, clientSecret);
      will(returnValue(true));

    }});

    tokenSecurity.authenticateClient(clientId, clientSecret);
  }

  @Test(expected = TokenErrorResponse.class)
  public void authenticateClientFails() throws Exception {
    final String clientId = "client_id";
    final String clientSecret = "client_secret";

    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(clientId, clientSecret);
      will(returnValue(false));
    }});

    tokenSecurity.authenticateClient(clientId, clientSecret);
  }

  @Test
  public void validAuthCode() throws Exception {
    final ProvidedAuthorizationCode authorizationCode = new ProvidedAuthorizationCode("code", "client_id", "client_secret");
    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(authorizationCode.clientId, authorizationCode.clientSecret);
      will(returnValue(true));

      oneOf(tokenCreationVerifier).verify(authorizationCode.value, authorizationCode.clientId);
      will(returnValue(true));
    }});

    tokenSecurity.validate(authorizationCode);

  }

  @Test(expected = TokenErrorResponse.class)
  public void wrongClientCredentials() throws Exception {
    final ProvidedAuthorizationCode authorizationCode = new ProvidedAuthorizationCode("code1", "client_id1", "client_secret1");


    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(authorizationCode.clientId, authorizationCode.clientSecret);
      will(returnValue(false));
    }});

    tokenSecurity.validate(authorizationCode);
  }

  @Test(expected = TokenErrorResponse.class)
  public void notExistingCode() throws Exception {
    final ProvidedAuthorizationCode code = new ProvidedAuthorizationCode("code2", "client_id2", "client_secret2");

    context.checking(new Expectations() {{
      oneOf(clientAuthentication).authenticate(code.clientId, code.clientSecret);
      will(returnValue(true));

      oneOf(tokenCreationVerifier).verify(code.value, code.clientId);
      will(returnValue(false));
    }});

    tokenSecurity.validate(code);
  }
}