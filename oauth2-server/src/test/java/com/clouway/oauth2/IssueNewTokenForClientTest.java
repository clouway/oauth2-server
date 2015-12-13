package com.clouway.oauth2;

import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.http.ParamRequest;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsPrint;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenRepository;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class IssueNewTokenForClientTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  TokenRepository tokenRepository;

  @Mock
  ClientAuthorizationRepository clientAuthorizationRepository;

  @Test
  public void happyPath() throws IOException {
    IssueNewTokenActivity controller = new IssueNewTokenActivity(tokenRepository, clientAuthorizationRepository);
    final Client client = aNewClient().build();

    context.checking(new Expectations() {{
      oneOf(clientAuthorizationRepository).authorize(client, "::auth_code::");
      will(returnValue(Optional.of(new Authorization("", "", "::auth_code::", "::redirect_uri::", "::user_id::"))));

      oneOf(tokenRepository).issueToken("::user_id::", Optional.<String>absent());
      will(returnValue(new Token("::token::", "berer", "", "::user_id", 10L, new Date())));
    }});


    Response response = controller.execute(client, new ParamRequest(ImmutableMap.of("code", "::auth_code::", "redirect_uri", "::redirect_uri::")));
    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("::token::"));
  }

  @Test
  public void clientWasNotAuthorized() throws IOException {
    final IssueNewTokenActivity controller = new IssueNewTokenActivity(tokenRepository, clientAuthorizationRepository);
    final Client client = aNewClient().build();

    context.checking(new Expectations() {{
      oneOf(clientAuthorizationRepository).authorize(client, "::auth_code1::");
      will(returnValue(Optional.absent()));
    }});

    Response response = controller.execute(client, new ParamRequest(ImmutableMap.of("code", "::auth_code1::", "redirect_uri", "::redirect_uri1::")));
    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("invalid_grant"));
  }

}