package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

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

  private Request clientAuthRequest(String authorizationValue) {
    return new ByteRequest(
            ImmutableMap.<String, String>of(),
            ImmutableMap.of("Authorization", authorizationValue)
    );
  }

}