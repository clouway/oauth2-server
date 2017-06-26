package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsText;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientFinder;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class AuthorizeClientsTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  ClientFinder clientFinder;

  @Mock
  ClientActivity clientActivity;

  private ClientController handler;

  @Before
  public void createHandler() {
    handler = new ClientController(clientFinder, clientActivity);
  }

  @Test
  public void happyPath() throws IOException {
    final Client anyRegisteredClient = newClient("fe72722a40de846e03865cb3b582aed57841ac71", "857613db7b18232c72a5093ad19dbc6df74a139e");

    final Request anyRequest = new ParamRequest(ImmutableMap.<String, String>of());
    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient("fe72722a40de846e03865cb3b582aed57841ac71");
      will(returnValue(Optional.of(anyRegisteredClient)));

      oneOf(clientActivity).execute(anyRegisteredClient, anyRequest, anyInstantTime);
      will(returnValue(new RsText("::body::")));
    }});

    Response response = handler.handleAsOf(
            anyRequest, new ClientCredentials("fe72722a40de846e03865cb3b582aed57841ac71", "857613db7b18232c72a5093ad19dbc6df74a139e"),
            anyInstantTime
    );

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("::body::"));
  }

  @Test
  public void clientWasNotFound() throws IOException {
    final Request anyRequest = new ParamRequest(ImmutableMap.<String, String>of());

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient("::unknown client id::");
      will(returnValue(Optional.absent()));
    }});

    Response response = handler.handleAsOf(anyRequest, new ClientCredentials("::unknown client id::", "::any secret::"), new DateTime());

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("unauthorized_client"));
  }

  @Test
  public void clientSecretNotMatch() throws IOException {
    final Client anyRegisteredClient = newClient(
            "::known client id::", "::client secrent::"
    );

    final Request anyRequest = new ParamRequest(ImmutableMap.<String, String>of());

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient("::known client id::");
      will(returnValue(Optional.of(anyRegisteredClient)));
    }});

    Response response = handler.handleAsOf(
            anyRequest, new ClientCredentials("::known client id::", "::client secret that will not match::"),
            new DateTime()
    );

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("unauthorized_client"));
  }

  @Test
  public void publicClient() throws IOException {
    final Client anyRegisteredClient = aNewClient().withId("::client id::").publicOne().build();

    final Request anyRequest = new ParamRequest(ImmutableMap.<String, String>of());
    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient("::client id::");
      will(returnValue(Optional.of(anyRegisteredClient)));

      oneOf(clientActivity).execute(anyRegisteredClient, anyRequest, anyInstantTime);
      will(returnValue(new RsText("::body::")));
    }});

    Response response = handler.handleAsOf(
            anyRequest, new ClientCredentials("::client id::", ""),
            anyInstantTime
    );

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("::body::"));
  }

  private Client newClient(String clientId, String secret) {
    return aNewClient().withId(clientId).withSecret(secret).build();
  }

}