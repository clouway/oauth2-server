package com.clouway.oauth2;

import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.testing.FakeRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.Collections;

import static com.clouway.oauth2.TokenBuilder.aNewToken;
import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RevokeTokensTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientFinder clientFinder = context.mock(ClientFinder.class);
  private Tokens tokens = context.mock(Tokens.class);

  private DateTime anyInstantenousTime = new DateTime();

  private RevokeTokenController controller = new RevokeTokenController(clientFinder, tokens);

  @Test
  public void happyPath() throws Exception {
    context.checking(new Expectations() {{
      oneOf(tokens).findTokenAvailableAt(with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(aNewToken().forClient("::client x::").build())));

      oneOf(clientFinder).findClient("::client x::");
      will(returnValue(Optional.of(aNewClient().withId("::client x::").withSecret("::client secret::").build())));

      oneOf(tokens).revokeToken("::any token::");
    }});

    Response response = controller.handleAsOf(
            newTokenRequest("::any token::"), new ClientCredentials("::client x::", "::client secret::"), anyInstantenousTime);

    assertThat(response.status().code, is(equalTo(HttpURLConnection.HTTP_OK)));
  }

  @Test
  public void tokenWasNotFound() throws Exception {
    context.checking(new Expectations() {{
      oneOf(tokens).findTokenAvailableAt(with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.absent()));
    }});

    Response response = controller.handleAsOf(
            newTokenRequest("::any token::"), new ClientCredentials("::client x::", "::client secret::"), anyInstantenousTime);

    assertThat(new RsPrint(response).print(), is(equalTo(new RsPrint(OAuthError.invalidRequest()).print())));
  }

  @Test
  public void clientWasNotFound() throws Exception {
    context.checking(new Expectations() {{
      oneOf(tokens).findTokenAvailableAt(with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(aNewToken().forClient("::client x::").build())));

      oneOf(clientFinder).findClient("::client x::");
      will(returnValue(Optional.absent()));
    }});

    Response response = controller.handleAsOf(
            newTokenRequest("::any token::"), new ClientCredentials("::client x::", "::broken client secret::"), anyInstantenousTime);

    assertThat(new RsPrint(response).print(), is(equalTo(new RsPrint(OAuthError.invalidClient()).print())));
  }

  @Test
  public void clientSecretIsNotMatching() throws Exception {
    context.checking(new Expectations() {{
      oneOf(tokens).findTokenAvailableAt(with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(aNewToken().forClient("::client x::").build())));

      oneOf(clientFinder).findClient(with(any(String.class)));
      will(returnValue(Optional.of(aNewClient().withId("::client x::").withSecret("::client secret::").build())));
    }});

    Response response = controller.handleAsOf(
            newTokenRequest("::any token::"), new ClientCredentials("::client x::", "::broken client secret::"), anyInstantenousTime);

    assertThat(new RsPrint(response).print(), is(equalTo(new RsPrint(OAuthError.invalidClient()).print())));
  }


  private FakeRequest newTokenRequest(String token) {
    return new FakeRequest(
            ImmutableMap.of("token", token),
            Collections.<String, String>emptyMap()
    );
  }


}