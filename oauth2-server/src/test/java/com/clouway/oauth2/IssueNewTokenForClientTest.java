package com.clouway.oauth2;

import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientKeyStore;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.IdentityFinder;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.LinkedHashMap;

import static com.clouway.oauth2.IdentityBuilder.aNewIdentity;
import static com.clouway.oauth2.PemKeyGenerator.generatePrivateKey;
import static com.clouway.oauth2.TokenBuilder.aNewToken;
import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class IssueNewTokenForClientTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private Tokens tokens = context.mock(Tokens.class);

  private ClientAuthorizationRepository clientAuthorizationRepository = context.mock(ClientAuthorizationRepository.class);

  private IdentityFinder identityFinder = context.mock(IdentityFinder.class);

  private ClientKeyStore keyStore = context.mock(ClientKeyStore.class);

  private IssueNewTokenActivity controller = new IssueNewTokenActivity(tokens, identityFinder, clientAuthorizationRepository, keyStore);

  @Test
  public void happyPath() throws IOException {
    IssueNewTokenActivity controller = new IssueNewTokenActivity(tokens, identityFinder, clientAuthorizationRepository, keyStore);
    final Client client = aNewClient().build();
    final DateTime anyTime = new DateTime();
    final Identity identity = aNewIdentity().withId("::user_id::").build();

    context.checking(new Expectations() {{
      oneOf(clientAuthorizationRepository).findAuthorization(with(any(Client.class)), with(any(String.class)));
      will(returnValue(Optional.of(new Authorization("", "", "::user_id::", "::auth_code::", Collections.<String>emptySet(), Collections.singleton("::redirect_uri::")))));

      oneOf(tokens).issueToken(GrantType.AUTHORIZATION_CODE, client, identity, Collections.<String>emptySet(), anyTime);
      will(returnValue(new TokenResponse(true, aNewToken().withValue("::token::").build(), "::refresh token::")));

      oneOf(identityFinder).findIdentity(with(any(String.class)), with(any(GrantType.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(identity)));

      oneOf(keyStore).publicCertificates();
      will(returnValue(new LinkedHashMap() {{
        put("53r7IfiCaT3",generatePrivateKey());
      }}));
    }});

    Response response = controller.execute(client, new ParamRequest(ImmutableMap.of("code", "::auth_code::", "redirect_uri", "::redirect_uri::")), anyTime);
    String body = new RsPrint(response).printBody();

    assertThat(body,containsString("id_token"));
    assertThat(body, containsString("::token::"));
  }

  @Test
  public void tokenCannotBeIssued() throws Exception {
    IssueNewTokenActivity controller = new IssueNewTokenActivity(tokens, identityFinder, clientAuthorizationRepository, keyStore);
    final Client client = aNewClient().build();
    final DateTime anyTime = new DateTime();
    final Identity identity = aNewIdentity().withId("::user_id::").build();

    context.checking(new Expectations() {{
      oneOf(clientAuthorizationRepository).findAuthorization(with(any(Client.class)), with(any(String.class)));
      will(returnValue(Optional.of(new Authorization("", "", "::user_id::", "::auth_code::", Collections.<String>emptySet(), Collections.singleton("::redirect_uri::")))));

      oneOf(tokens).issueToken(GrantType.AUTHORIZATION_CODE, client, identity, Collections.<String>emptySet(), anyTime);
      will(returnValue(new TokenResponse(false, null, "")));

      oneOf(identityFinder).findIdentity(with(any(String.class)), with(any(GrantType.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(identity)));

      oneOf(keyStore).publicCertificates();
      will(returnValue(new LinkedHashMap() {{
        put("53r7IfiCaT3", generatePrivateKey());
      }}));

    }});

    Response response = controller.execute(client, new ParamRequest(ImmutableMap.of("code", "::auth_code::", "redirect_uri", "::redirect_uri::")), anyTime);
    String body = new RsPrint(response).printBody();

    assertThat(response.status().code, is(HttpURLConnection.HTTP_BAD_REQUEST));
    assertThat(body, containsString("invalid_request"));
  }

  @Test
  public void controllerCallsClientAuthRepositoryWithCorrectValues() throws IOException {
    final Client anyClient = aNewClient().build();

    context.checking(new Expectations() {{
      oneOf(clientAuthorizationRepository).findAuthorization(anyClient, "::auth code::");
      will(returnValue(Optional.absent()));
    }});

    controller.execute(anyClient, new ParamRequest(
                    ImmutableMap.of("code", "::auth code::")),
            null
    );
  }

  @Test
  public void clientWasNotAuthorized() throws IOException {
    final IssueNewTokenActivity controller = new IssueNewTokenActivity(tokens, identityFinder, clientAuthorizationRepository, keyStore);
    final Client client = aNewClient().build();
    final DateTime anyTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(clientAuthorizationRepository).findAuthorization(with(any(Client.class)), with(any(String.class)));
      will(returnValue(Optional.absent()));
    }});

    Response response = controller.execute(client, new ParamRequest(ImmutableMap.of("code", "::auth_code1::", "redirect_uri", "::redirect_uri1::")), anyTime);
    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("invalid_grant"));
  }

}