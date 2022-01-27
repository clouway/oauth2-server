package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsText;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.codechallenge.CodeChallenge;
import com.clouway.oauth2.codechallenge.CodeVerifier;
import com.clouway.oauth2.common.DateTime;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static com.clouway.friendlyserve.testing.FakeRequest.aNewRequest;
import static com.clouway.oauth2.authorization.AuthorizationBuilder.newAuthorization;
import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class CodeExchangeVerificationFlowTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private AuthorizedClientActivity clientActivity = context.mock(AuthorizedClientActivity.class);
  private CodeVerifier codeVerifier = context.mock(CodeVerifier.class);

  private CodeExchangeVerificationFlow flow = new CodeExchangeVerificationFlow(codeVerifier, clientActivity);

  @Test
  public void happyPath() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().withCodeChallenge(new CodeChallenge("::codeChallenge::", "::method::")).withCode("::any code::").withId("::user_id::").build();

    final Request request = aNewRequest().param("code", "::any code::").param("code_verifier", "::codeVerifier::").build();

    context.checking(new Expectations() {{

      oneOf(codeVerifier).verify(new CodeChallenge("::codeChallenge::", "::method::"), "::codeVerifier::");
      will(returnValue(true));

      oneOf(clientActivity).execute(anyAuth, anyClient, request, anyInstantTime);
      will(returnValue(new RsText("::response::")));

    }});

    Response response = flow.execute(anyAuth, anyClient, request, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), is(equalTo("::response::")));
  }

  @Test
  public void codeVerificationFailed() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().withCodeChallenge(new CodeChallenge("::codeChallenge::", "::method::")).build();

    final Request request = aNewRequest().param("code", "::any code::").param("code_verifier", "::codeVerifier::").build();

    context.checking(new Expectations() {{

      oneOf(codeVerifier).verify(new CodeChallenge("::codeChallenge::", "::method::"), "::codeVerifier::");
      will(returnValue(false));
    }});

    Response response = flow.execute(anyAuth, anyClient, request, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), containsString("code verification failed"));
  }

  @Test
  public void codeVerifierNotProvidedInRequest() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().withCodeChallenge(new CodeChallenge("::codeChallenge::", "::method::")).withCode("::any code::").withId("::user_id::").build();

    final Request request = aNewRequest().param("code", "::any code::").build();

    context.checking(new Expectations() {{

      oneOf(codeVerifier).verify(new CodeChallenge("::codeChallenge::", "::method::"), "");
      will(returnValue(false));
    }});

    flow.execute(anyAuth, anyClient, request, anyInstantTime);
  }
}