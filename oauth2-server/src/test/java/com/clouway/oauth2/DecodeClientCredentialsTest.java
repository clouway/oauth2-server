package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;

import static com.clouway.friendlyserve.testing.FakeRequest.aNewRequest;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class DecodeClientCredentialsTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientRequest clientRequest = context.mock(ClientRequest.class);

  private ClientAuthenticationCredentialsRequest handler = new ClientAuthenticationCredentialsRequest(clientRequest);

  @Test
  public void happyPath() {
    final DateTime anyInstantTime = new DateTime();
    final Request clientAuthRequest = clientAuthRequest(
            "Basic ZmU3MjcyMmE0MGRlODQ2ZTAzODY1Y2IzYjU4MmFlZDU3ODQxYWM3MTo4NTc2MTNkYjdiMTgyMzJjNzJhNTA5M2FkMTlkYmM2ZGY3NGExMzll"
    );

    context.checking(new Expectations() {{
      oneOf(clientRequest).handleAsOf(
              clientAuthRequest, new ClientCredentials(
                      "fe72722a40de846e03865cb3b582aed57841ac71",
                      "857613db7b18232c72a5093ad19dbc6df74a139e"
              ), anyInstantTime);
    }});


    handler.handleAsOf(clientAuthRequest, anyInstantTime);
  }

  @Test
  public void headerIsNotForBasicAuthorization() {
    final Request clientAuthRequest = clientAuthRequest(
            "::some header value::"
    );
    Response response = handler.handleAsOf(clientAuthRequest, anyInstantTime());
    assertThat(response.status().code, is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
  }

  @Test
  public void basicHeaderIsNotEncodedWithBase64() {
    final Request clientAuthRequest = clientAuthRequest(
            "Basic z"
    );
    Response response = handler.handleAsOf(clientAuthRequest, anyInstantTime());
    assertThat(response.status().code, is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
  }

  @Test
  public void clientIdAndClientSecretAreNotCorrectlySeparated() {
    final Request clientAuthRequest = clientAuthRequest(
            "Basic dGVzdGlkdGVzdHNlY3JldA==" // is encoding of: testidtestsecret
    );
    Response response = handler.handleAsOf(clientAuthRequest, anyInstantTime());
    assertThat(response.status().code, is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
  }

  @Test
  public void authenticatePublicClient() throws Exception {
    final Request clientAuthRequest = aNewRequest().param("client_id", "::client id::").build();
    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(clientRequest).handleAsOf(
              clientAuthRequest, new ClientCredentials(
                      "::client id::",
                      ""
              ), anyInstantTime);
    }});

    handler.handleAsOf(clientAuthRequest, anyInstantTime);
  }

  private DateTime anyInstantTime() {
    return new DateTime();
  }

  private Request clientAuthRequest(String authorizationValue) {
    return new ByteRequest(
            ImmutableMap.<String, String>of(),
            ImmutableMap.of("Authorization", authorizationValue)
    );
  }

}