package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsPrint;
import com.clouway.oauth2.http.RsText;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class AuthorizeClientsTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  ClientRepository clientRepository;

  @Mock
  ClientActivity clientActivity;

  ClientController handler;

  @Before
  public void createHandler() {
    handler = new ClientController(clientRepository, clientActivity);
  }

  @Test
  public void happyPath() throws IOException {
    final Client anyRegisteredClient = newClient("fe72722a40de846e03865cb3b582aed57841ac71", "857613db7b18232c72a5093ad19dbc6df74a139e");

    final Request request = clientAuthRequest("Basic ZmU3MjcyMmE0MGRlODQ2ZTAzODY1Y2IzYjU4MmFlZDU3ODQxYWM3MTo4NTc2MTNkYjdiMTgyMzJjNzJhNTA5M2FkMTlkYmM2ZGY3NGExMzll");

    context.checking(new Expectations() {{
      oneOf(clientRepository).findById("fe72722a40de846e03865cb3b582aed57841ac71");
      will(returnValue(Optional.of(anyRegisteredClient)));

      oneOf(clientActivity).execute(anyRegisteredClient, request);
      will(returnValue(new RsText("::body::")));
    }});

    Response response = handler.ack(request);

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("::body::"));
  }

  @Test
  public void clientWasNotFound() throws IOException {
    final Request request = clientAuthRequest("Basic ZmU3MjcyMmE0MGRlODQ2ZTAzODY1Y2IzYjU4MmFlZDU3ODQxYWM3MTo4NTc2MTNkYjdiMTgyMzJjNzJhNTA5M2FkMTlkYmM2ZGY3NGExMzll");

    context.checking(new Expectations() {{
      oneOf(clientRepository).findById("fe72722a40de846e03865cb3b582aed57841ac71");
      will(returnValue(Optional.absent()));
    }});

    Response response = handler.ack(request);

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("unauthorized_client"));
  }

  @Test
  public void clientSecretNotMatch() throws IOException {
    final Client anyRegisteredClient = newClient(
            "fe72722a40de846e03865cb3b582aed57841ac71", "::dummy_secret::"
    );

    final Request request = clientAuthRequest("Basic ZmU3MjcyMmE0MGRlODQ2ZTAzODY1Y2IzYjU4MmFlZDU3ODQxYWM3MTo4NTc2MTNkYjdiMTgyMzJjNzJhNTA5M2FkMTlkYmM2ZGY3NGExMzll");

    context.checking(new Expectations() {{
      oneOf(clientRepository).findById("fe72722a40de846e03865cb3b582aed57841ac71");
      will(returnValue(Optional.of(anyRegisteredClient)));
    }});

    Response response = handler.ack(request);

    String body = new RsPrint(response).printBody();

    assertThat(body, containsString("unauthorized_client"));
  }

  private Request clientAuthRequest(String authorizationValue) {
    return new ByteRequest(
            ImmutableMap.<String, String>of(),
            ImmutableMap.of("Authorization", authorizationValue)
    );
  }

  private Client newClient(String clientId, String secret) {
    return new Client(
            clientId,
            secret,
            "::test_client::",
            "::url::",
            "::description::",
            "::redirect::"
    );
  }

}