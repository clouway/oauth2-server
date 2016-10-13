package com.clouway.oauth2;

import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.Status;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;

import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class ResourceOwnerClientAuthorizationTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  ClientAuthorizationRepository clientAuthorizationRepository;

  @Mock
  ClientRepository clientRepository;
  private ClientAuthorizationActivity activity;

  @Before
  public void setUpActivity() {
    activity = new ClientAuthorizationActivity(clientRepository, clientAuthorizationRepository);
  }

  @Test
  public void happyPath() throws IOException {
    ParamRequest authRequest = new ParamRequest(
            ImmutableMap.of("response_type", "code", "client_id", "::client_id::", "redirect_uri", "http://example.com/callback")
    );
    final Client anyExistingClient = aNewClient().withRedirectUrl("http://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientRepository).findById("::client_id::");
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(clientAuthorizationRepository).authorize(anyExistingClient, "user1", "code");
      will(returnValue(Optional.of(new Authorization("code", "::client_id::", "1234", Collections.singleton("http://example.com/callback"), "identityId"))));
    }});

    Response response = activity.execute("user1", authRequest);
    Status status = response.status();

    assertThat(status.code, is(HttpURLConnection.HTTP_MOVED_TEMP));
    assertThat(status.redirectUrl, is(containsString("http://example.com/callback?code=1234")));
  }

  @Test
  public void redirectUrlWasNotRequested() throws Exception {
    ParamRequest authRequest = new ParamRequest(
            ImmutableMap.of("response_type", "code", "client_id", "::another client::")
    );
    final Client anyExistingClient = aNewClient().withRedirectUrl("http://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientRepository).findById(with(any(String.class)));
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(clientAuthorizationRepository).authorize(anyExistingClient, "user1", "code");
      will(returnValue(Optional.of(new Authorization("code", "::client_id::", "1234", Collections.singleton("http://example.com/callback"), "identityId"))));
    }});

    Response response = activity.execute("user1", authRequest);
    Status status = response.status();

    assertThat(status.code, is(HttpURLConnection.HTTP_MOVED_TEMP));
    assertThat(status.redirectUrl, is(containsString("http://example.com/callback?code=1234")));
  }

  @Test
  public void clientWasNotAuthorized() {
    ParamRequest authRequest = new ParamRequest(
            ImmutableMap.of("response_type", "code", "client_id", "::client_id::", "redirect_uri", "https://example.com/callback")
    );
    final Client anyExistingClient = aNewClient().withRedirectUrl("https://example.com/callback").build();

    context.checking(new Expectations() {{

      oneOf(clientRepository).findById("::client_id::");
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(clientAuthorizationRepository).authorize(anyExistingClient, "::identity_id::", "code");
      will(returnValue(Optional.absent()));
    }});

    Response response = activity.execute("::identity_id::", authRequest);
    Status status = response.status();

    assertThat(status.code, is(HttpURLConnection.HTTP_MOVED_TEMP));
    assertThat(status.redirectUrl, is(equalTo("https://example.com/callback?error=access_denied")));
  }

  @Test
  public void unknownClient() {
    ParamRequest authRequest = new ParamRequest(
            ImmutableMap.of("response_type", "code", "client_id", "::client_id::", "redirect_uri", "https://example.com")
    );
    context.checking(new Expectations() {{
      oneOf(clientRepository).findById("::client_id::");
      will(returnValue(Optional.absent()));
    }});

    Response response = activity.execute("::any_identity_id::", authRequest);
    Status status = response.status();
    assertThat(status.code, is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void clientRedirectUrlIsNotMatching() throws Exception {
    ParamRequest authRequest = new ParamRequest(
            ImmutableMap.of(
                    "response_type", "code",
                    "client_id", "::client_id::",
                    "redirect_uri", "https://example.com"
            )
    );

    final Client anyExistingClient = aNewClient().withRedirectUrl("https://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientRepository).findById("::client_id::");
      will(returnValue(Optional.of(anyExistingClient)));
    }});

    Response response = activity.execute("::identity_id::", authRequest);
    Status status = response.status();
    assertThat(status.code, is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

}