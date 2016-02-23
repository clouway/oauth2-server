package com.clouway.oauth2;

import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.http.ParamRequest;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.Status;
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
public class ResourceOwnerClientAuthorizationTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  ClientAuthorizationRepository clientRepository;

  @Test
  public void happyPath() throws IOException {
    ParamRequest authRequest = new ParamRequest(
            ImmutableMap.of("response_type", "code", "client_id", "::client_id::", "redirect_uri", "https://example.com")
    );
    final Client anyExistingClient = aNewClient().withRedirectUrl("http://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientRepository).authorize(anyExistingClient, "user1", "code");
      will(returnValue(Optional.of(new Authorization("code", "::client_id::", "1234", "::redirect_url::", "userId"))));
    }});

    Response response = new AuthorizationActivity(clientRepository).execute(anyExistingClient, "user1", authRequest);
    Status status = response.status();

    assertThat(status.code, is(HttpURLConnection.HTTP_MOVED_TEMP));
    assertThat(status.redirectUrl, is(containsString("http://example.com/callback?code=1234")));
  }

  @Test
  public void clientWasNotAuthorized() {
    ParamRequest authRequest = new ParamRequest(
            ImmutableMap.of("response_type", "code", "client_id", "::client_id::", "redirect_uri", "https://example.com")
    );
    final Client anyExistingClient = aNewClient().withRedirectUrl("https://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientRepository).authorize(anyExistingClient, "user1", "code");
      will(returnValue(Optional.absent()));
    }});

    Response response = new AuthorizationActivity(clientRepository).execute(anyExistingClient, "user1", authRequest);
    Status status = response.status();

    assertThat(status.code, is(HttpURLConnection.HTTP_MOVED_TEMP));
    assertThat(status.redirectUrl, is(equalTo("https://example.com/callback?error=access_denied")));
  }

}