package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.http.ParamRequest;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsPrint;
import com.clouway.oauth2.http.RsText;
import com.clouway.oauth2.http.Status;
import com.clouway.oauth2.user.UserIdFinder;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class IdentityControllerTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  ClientRepository clientRepository;

  @Mock
  UserIdFinder userIdFinder;

  @Mock
  IdentityActivity identityActivity;

  @Test
  public void happyPath() throws IOException {
    IdentityController identityController = new IdentityController(clientRepository, userIdFinder, identityActivity);
    final Client client = aNewClient().build();
    final Request request = new ParamRequest(ImmutableMap.of(
            "client_id", "::client_id::"
    ));

    context.checking(new Expectations() {{
      oneOf(userIdFinder).find(request);
      will(returnValue(Optional.of("::user_id::")));

      oneOf(clientRepository).findById("::client_id::");
      will(returnValue(Optional.of(client)));


      oneOf(identityActivity).execute(client, "::user_id::", request);
      will(returnValue(new RsText("test response")));
    }});

    Response response = identityController.ack(request);
    assertThat(new RsPrint(response).printBody(), is(equalTo("test response")));
  }

  @Test
  public void userWasNotAuthorized() throws IOException {
    IdentityController identityController = new IdentityController(clientRepository, userIdFinder, identityActivity);
    final Request request = new ParamRequest(ImmutableMap.<String, String>of("client_id","::client1::"));
    final Client anyExistingClient = aNewClient().build();

    context.checking(new Expectations() {{
      oneOf(clientRepository).findById("::client1::");
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(userIdFinder).find(request);
      will(returnValue(Optional.absent()));
    }});

    Response response = identityController.ack(request);
    Status status = response.status();

    assertThat(status.code, is(equalTo(HttpURLConnection.HTTP_MOVED_TEMP)));
    assertThat(status.redirectUrl, is(equalTo("/r/oauth/login?redirectUrl=%2F%3Fclient_id%3D%3A%3Aclient1%3A%3A")));
  }

  @Test
  public void unknownClient() throws IOException {
    IdentityController identityController = new IdentityController(clientRepository, userIdFinder, identityActivity);
    final Request request = new ParamRequest(ImmutableMap.of("client_id", "::client_id::"));

    context.checking(new Expectations() {{
      oneOf(clientRepository).findById("::client_id::");
      will(returnValue(Optional.absent()));
    }});

    Response response = identityController.ack(request);
    Status status = response.status();

    assertThat(status.code, is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
    assertThat(new RsPrint(response).printBody(),containsString("unauthorized_client"));
  }

}