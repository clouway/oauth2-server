package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class ParseClientCredentialsFromAuthorizationHeaderTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientRequest clientRequest = context.mock(ClientRequest.class);

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

    AuthorizationHeaderCredentialsRequest handler = new AuthorizationHeaderCredentialsRequest(clientRequest);
    handler.handleAsOf(clientAuthRequest, anyInstantTime);
  }

  @Test
  public void headerIsNotWithBasicAuthorization() {
    final Request clientAuthRequest = clientAuthRequest(
            "ZmU3MjcyMmE0MGRlODQ2ZTAzODY1Y2IzYjU4MmFlZDU3ODQxYWM3MTo4NTc2MTNkYjdiMTgyMzJjNzJhNTA5M2FkMTlkYmM2ZGY3NGExMzll"
    );
    Response response = new AuthorizationHeaderCredentialsRequest(clientRequest).handleAsOf(clientAuthRequest, anyInstantTime());
    assertThat(response.status().code, is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
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