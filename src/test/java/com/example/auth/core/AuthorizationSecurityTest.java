package com.example.auth.core;

import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class AuthorizationSecurityTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientFinder clientFinder = context.mock(ClientFinder.class);
  private AuthorizationStore authorizationStore = context.mock(AuthorizationStore.class);
  private TokenGenerator tokenGenerator = context.mock(TokenGenerator.class);

  private AuthorizationSecurity authorizationSecurity = new AuthorizationSecurityImpl(clientFinder, authorizationStore, tokenGenerator);

  @Test
  public void happyPath() throws Exception {
    final AuthorizationRequest request = new AuthorizationRequest("code", "id");
    final Client client = new Client("id", "secret", "name", "url", "description", "redirectURI");
    final Authorization authorization = new Authorization("code", "id", "token", "redirectURI");

    context.checking(new Expectations() {{
      oneOf(clientFinder).findById("id");
      will(returnValue(Optional.of(client)));

      oneOf(tokenGenerator).generate();
      will(returnValue("token"));

      oneOf(authorizationStore).register(authorization);
    }});

    authorizationSecurity.auth(request);
  }

  @Test(expected = AuthorizationErrorResponse.class)
  public void notExistingClient() throws Exception {
    final AuthorizationRequest request = new AuthorizationRequest("code", "id1");

    context.checking(new Expectations() {{
      oneOf(clientFinder).findById("id1");
      will(returnValue(Optional.absent()));
    }});

    authorizationSecurity.auth(request);
  }
}