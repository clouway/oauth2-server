package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsText;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

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

  private ClientAuthorizationRepository repository = context.mock(ClientAuthorizationRepository.class);

  private AuthorizedClientActivity authorizedClientActivity = context.mock(AuthorizedClientActivity.class);

  AuthCodeAuthorization authorization = new AuthCodeAuthorization(repository, authorizedClientActivity);


  @Test
  public void happyPath() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().withId("::user_id::").build();

    final Request request = aNewRequest().param("code", "::any code::").build();

    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization(anyClient, "::any code::", anyInstantTime);
      will(returnValue(Optional.of(anyAuth)));

      oneOf(authorizedClientActivity).execute(anyAuth, anyClient, request, anyInstantTime);
      will(returnValue(new RsText("::response::")));

    }});
    Response response = authorization.execute(anyClient, request, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), is(equalTo("::response::")));
  }

  @Test
  public void authorizationNotFound() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();

    final Request request = aNewRequest().param("code", "::nonExistentAuth::").param("code_verifier", "::codeVerifier::").build();

    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization(anyClient, "::nonExistentAuth::", anyInstantTime);
      will(returnValue(Optional.absent()));
    }});

    Response response = authorization.execute(anyClient, request, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), containsString("invalid_grant"));
  }
}