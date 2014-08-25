package com.example.auth.core.authorization;

import com.example.auth.core.client.Client;
import com.example.auth.core.client.ClientRepository;
import com.example.auth.core.token.TokenGenerator;
import com.example.auth.core.user.UserIdFinder;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class ClientAuthorizationSecurityTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientRepository clientFinder = context.mock(ClientRepository.class);
  private ClientAuthorizationRepository clientAuthorizationRepository = context.mock(ClientAuthorizationRepository.class);
  private TokenGenerator tokenGenerator = context.mock(TokenGenerator.class);

  @Mock
  private UserIdFinder userIdFinder;

  private AuthorizationSecurity authorizationSecurity;


  @Before
  public void setUp() throws Exception {
    authorizationSecurity = new AuthorizationSecurityImpl(clientFinder, clientAuthorizationRepository, tokenGenerator, userIdFinder);
  }

  @Test
  public void happyPath() throws Exception {
    final AuthorizationRequest request = new AuthorizationRequest("code", "id", "sessionId");
    final Client client = new Client("id", "secret", "name", "url", "description", "redirectURI");
    final Authorization authorization = new Authorization("code", "id", "token", "redirectURI", "userId");

    context.checking(new Expectations() {{
      oneOf(clientFinder).findById("id");
      will(returnValue(Optional.of(client)));

      oneOf(tokenGenerator).generate();
      will(returnValue("token"));

      oneOf(userIdFinder).find("sessionId");
      will(returnValue("userId"));

      oneOf(clientAuthorizationRepository).register(authorization);
    }});

    authorizationSecurity.auth(request);
  }

  @Test(expected = AuthorizationErrorResponse.class)
  public void notExistingClient() throws Exception {
    final AuthorizationRequest request = new AuthorizationRequest("code", "id1", "sessionId");

    context.checking(new Expectations() {{
      oneOf(clientFinder).findById("id1");
      will(returnValue(Optional.absent()));
    }});

    authorizationSecurity.auth(request);
  }
}