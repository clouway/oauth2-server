package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsText;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizer;
import com.clouway.oauth2.authorization.FindAuthorizationResult;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientCredentials;
import com.clouway.oauth2.common.DateTime;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.clouway.friendlyserve.testing.FakeRequest.aNewRequest;
import static com.clouway.oauth2.authorization.AuthorizationBuilder.newAuthorization;
import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public class AuthCodeAuthorizationTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientAuthorizer repository = context.mock(ClientAuthorizer.class);

  private AuthorizedClientActivity authorizedClientActivity = context.mock(AuthorizedClientActivity.class);

  AuthCodeAuthorization authorization = new AuthCodeAuthorization(repository, authorizedClientActivity);


  @Test
  public void happyPath() throws Exception {
    final Client anyClient = aNewClient().withId("::client id::").withSecret("::secret::").build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().withId("::user_id::").build();
    ClientCredentials credentials = new ClientCredentials("::client id::", "::secret::");

    final Request request = aNewRequest().param("code", "::any code::").build();
    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization("::client id::", "::any code::", anyInstantTime);
      will(returnValue(new FindAuthorizationResult.Success(anyAuth, anyClient)));

      oneOf(authorizedClientActivity).execute(anyAuth, anyClient, request, anyInstantTime);
      will(returnValue(new RsText("::response::")));

    }});

    Response response = authorization.handleAsOf(request, credentials, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), is(equalTo("::response::")));
  }

  @Test
  public void authorizationNotFound() throws Exception {
    final DateTime anyInstantTime = new DateTime();

    final Request request = aNewRequest().param("code", "::nonExistentAuth::").param("code_verifier", "::codeVerifier::").build();
    ClientCredentials credentials = new ClientCredentials("::client id::", "::any secret::");

    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization("::client id::", "::nonExistentAuth::", anyInstantTime);
      will(returnValue(new FindAuthorizationResult.NotFound()));
    }});

    Response response = authorization.handleAsOf(request, credentials, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), containsString("invalid_grant"));
  }


  @Test
  public void clientWasNotFound() throws IOException {
    final DateTime anyInstantTime = new DateTime();

    final Request request = aNewRequest().param("code", "::nonExistentAuth::").param("code_verifier", "::codeVerifier::").build();
    ClientCredentials credentials = new ClientCredentials("::client id::", "::any secret::");

    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization("::client id::", "::nonExistentAuth::", anyInstantTime);
      will(returnValue(new FindAuthorizationResult.ClientNotFound()));
    }});

    Response response = authorization.handleAsOf(request, credentials, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), containsString("unauthorized_client"));
  }

  @Test
  public void clientSecretNotMatch() throws IOException {
    final Client anyClient = aNewClient().withSecret("::secret::").build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().withId("::user_id::").build();
    ClientCredentials credentials = new ClientCredentials("::client id::", "::bad secret::");

    final Request request = aNewRequest().param("code", "::any code::").build();
    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization("::client id::", "::any code::", anyInstantTime);
      will(returnValue(new FindAuthorizationResult.Success(anyAuth, anyClient)));
    }});

    Response response = authorization.handleAsOf(request, credentials, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), containsString("unauthorized_client"));
  }

}