package com.clouway.oauth2;

import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientKeyStore;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Set;

import static com.clouway.oauth2.IdentityBuilder.aNewIdentity;
import static com.clouway.oauth2.PemKeyGenerator.generatePrivateKey;
import static com.clouway.oauth2.TokenBuilder.aNewToken;
import static com.clouway.oauth2.authorization.AuthorizationBuilder.newAuthorization;
import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class IssueNewTokenForClientTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private Tokens tokens = context.mock(Tokens.class);
  private ClientKeyStore keyStore = context.mock(ClientKeyStore.class);

  private IssueNewTokenActivity controller = new IssueNewTokenActivity(tokens, keyStore);

  @Test
  @SuppressWarnings("unchecked")
  public void happyPath() throws IOException {
    final DateTime anyTime = new DateTime();
    final Identity identity = aNewIdentity().withId("::user_id::").build();
    final Authorization anyAuhtorization = newAuthorization().build();

    context.checking(new Expectations() {{
      oneOf(keyStore).privateCertificates();
      will(returnValue(Collections.singletonMap("53r7IfiCaT3", generatePrivateKey())));

        oneOf(tokens).issueToken(with(any(GrantType.class)), with(any(Client.class)), with(any(Identity.class)), with(any(Set.class)), with(any(DateTime.class)));
      will(returnValue(new TokenResponse(true, aNewToken().withValue("::token::").build(), "::refresh token::")));
    }});

    Response response = controller.execute(aNewClient().build(), identity, anyAuhtorization.scopes, new ParamRequest(Collections.<String, String>emptyMap()), anyTime);
    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("id_token"));
    assertThat(body, containsString("::token::"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void noPrivateCertsAreConfigured() throws Exception {
    final DateTime anyTime = new DateTime();
    final Identity identity = aNewIdentity().withId("::user_id::").build();
    final Authorization anyAuhtorization = newAuthorization().build();

    context.checking(new Expectations() {{
      oneOf(keyStore).privateCertificates();
      will(returnValue(Collections.emptyMap()));

      oneOf(tokens).issueToken(with(any(GrantType.class)), with(any(Client.class)), with(any(Identity.class)), with(any(Set.class)), with(any(DateTime.class)));
      will(returnValue(new TokenResponse(true, aNewToken().withValue("::token::").build(), "::refresh token::")));
    }});

    Response response = controller.execute(aNewClient().build(), identity, anyAuhtorization.scopes, new ParamRequest(Collections.<String, String>emptyMap()), anyTime);
    String body = new RsPrint(response).printBody();

    assertThat(body, not(containsString("id_token")));
    assertThat(body, containsString("::token::"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void tokenCannotBeIssued() throws Exception {
    final Client client = aNewClient().build();
    final DateTime anyTime = new DateTime();
    final Identity identity = aNewIdentity().withId("::user_id::").build();

    context.checking(new Expectations() {{
      oneOf(tokens).issueToken(with(any(GrantType.class)), with(any(Client.class)), with(any(Identity.class)), with(any(Set.class)), with(any(DateTime.class)));
      will(returnValue(new TokenResponse(false, null, "")));
    }});

    Response response = controller.execute(client, identity, Collections.<String>emptySet(), new ParamRequest(Collections.<String, String>emptyMap()), anyTime);
    String body = new RsPrint(response).printBody();

    assertThat(response.status().code, is(HttpURLConnection.HTTP_BAD_REQUEST));
    assertThat(body, containsString("invalid_request"));
  }

  @Test
  public void parametersArePassed() throws Exception {
    final Client client = aNewClient().build();
    final DateTime anyTime = new DateTime();
    final Identity identity = aNewIdentity().withId("::user_id::").build();
    final Authorization authorization = newAuthorization().build();

    context.checking(new Expectations() {{
      oneOf(tokens).issueToken(GrantType.AUTHORIZATION_CODE, client, identity, authorization.scopes, anyTime);
      will(returnValue(new TokenResponse(false, null, "")));
    }});

    controller.execute(client, identity, authorization.scopes, new ParamRequest(Collections.<String, String>emptyMap()), anyTime);
  }

}