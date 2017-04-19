package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsText;
import com.clouway.friendlyserve.testing.ParamRequest;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.user.IdentityFinder;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;

import static com.clouway.oauth2.IdentityBuilder.aNewIdentity;
import static com.clouway.oauth2.authorization.AuthorizationBuilder.newAuthorization;
import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class AuthorizeClientWithAuthCodeTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientAuthorizationRepository repository = context.mock(ClientAuthorizationRepository.class);
  private IdentityFinder identityFinder = context.mock(IdentityFinder.class);
  private AuthorizedClientActivity clientActivity = context.mock(AuthorizedClientActivity.class);

  private AuthCodeAuthorization authCodeAuthorization = new AuthCodeAuthorization(repository, identityFinder, clientActivity);

  @Test
  public void happyPath() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().build();
    final Identity identity = aNewIdentity().withId("::user_id::").build();

    final Request request = new ParamRequest(Collections.singletonMap("code", "::any code::"));

    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization(with(any(Client.class)), with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(anyAuth)));

      oneOf(identityFinder).findIdentity(with(any(String.class)), with(any(GrantType.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(identity)));

      oneOf(clientActivity).execute(anyClient, identity, anyAuth.scopes, request, anyInstantTime);
      will(returnValue(new RsText("::response::")));

    }});

    Response response = authCodeAuthorization.execute(anyClient, request, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), is(equalTo("::response::")));
  }


  @Test
  public void authWasNotFound() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();

    final Request request = new ParamRequest(Collections.singletonMap("code", "::any code::"));

    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization(with(any(Client.class)), with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.absent()));
    }});

    Response response = authCodeAuthorization.execute(anyClient, request, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), containsString("invalid_grant"));
  }

  @Test
  public void identityWasNotFound() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().build();

    final Request request = new ParamRequest(Collections.singletonMap("code", "::any code::"));

    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization(with(any(Client.class)), with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(anyAuth)));

      oneOf(identityFinder).findIdentity(with(any(String.class)), with(any(GrantType.class)), with(any(DateTime.class)));
      will(returnValue(Optional.absent()));
    }});

    Response response = authCodeAuthorization.execute(anyClient, request, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), containsString("invalid_grant"));
  }

  @Test
  public void authorizationParamsArePassed() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();

    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization(anyClient, "::any code::", anyInstantTime);
      will(returnValue(Optional.absent()));
    }});

    authCodeAuthorization.execute(anyClient, new ParamRequest(Collections.singletonMap("code", "::any code::")), anyInstantTime);
  }

  @Test
  public void identityParamsArePassed() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().build();

    context.checking(new Expectations() {{
      oneOf(repository).findAuthorization(with(any(Client.class)), with(any(String.class)), with(any(DateTime.class)));
      will(returnValue(Optional.of(anyAuth)));

      oneOf(identityFinder).findIdentity(anyAuth.identityId, GrantType.AUTHORIZATION_CODE, anyInstantTime);
      will(returnValue(Optional.absent()));
    }});

    authCodeAuthorization.execute(anyClient, new ParamRequest(Collections.singletonMap("code", "::any code::")), anyInstantTime);
  }
}