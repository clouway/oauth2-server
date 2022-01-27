package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsText;
import com.clouway.friendlyserve.testing.RsPrint;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.codechallenge.CodeChallenge;
import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.token.FindIdentityRequest;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.Identity;
import com.clouway.oauth2.token.IdentityFinder;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static com.clouway.friendlyserve.testing.FakeRequest.aNewRequest;
import static com.clouway.oauth2.authorization.AuthorizationBuilder.newAuthorization;
import static com.clouway.oauth2.client.ClientBuilder.aNewClient;
import static com.clouway.oauth2.token.IdentityBuilder.aNewIdentity;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public class IdentityAuthorizationActivityTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private IdentityFinder identityFinder = context.mock(IdentityFinder.class);
  private AuthorizedIdentityActivity authorizedIdentityActivity = context.mock(AuthorizedIdentityActivity.class);

  private IdentityAuthorizationActivity identityAuthorizationActivity = new IdentityAuthorizationActivity(identityFinder, authorizedIdentityActivity);

  @Test
  public void happyPath() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().withCodeChallenge(new CodeChallenge("::codeChallenge::", "::method::")).withCode("::any code::").withId("::user_id::").build();
    final Identity identity = aNewIdentity().withId("::user_id::").build();

    final Request request = aNewRequest().param("code", "::any code::").param("code_verifier", "::codeVerifier::").build();

    context.checking(new Expectations() {{

      oneOf(identityFinder).findIdentity(new FindIdentityRequest("::user_id::", GrantType.AUTHORIZATION_CODE, anyInstantTime, Maps.<String, String>newHashMap(), anyAuth.clientId));
      will(returnValue(Optional.of(identity)));

      oneOf(authorizedIdentityActivity).execute(anyClient, identity, anyAuth.scopes, request, anyInstantTime, anyAuth.params);
      will(returnValue(new RsText("::response::")));

    }});

    Response response = identityAuthorizationActivity.execute(anyAuth, anyClient, request, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), is(equalTo("::response::")));
  }

  @Test
  public void identityNotFound() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().withCodeChallenge(new CodeChallenge("::codeChallenge::", "::method::")).withCode("::any code::").withId("::user_id::").build();

    final Request request = aNewRequest().param("code", "::any code::").param("code_verifier", "::codeVerifier::").build();

    context.checking(new Expectations() {{

      oneOf(identityFinder).findIdentity(new FindIdentityRequest("::user_id::", GrantType.AUTHORIZATION_CODE, anyInstantTime, Maps.<String, String>newHashMap(), anyAuth.clientId));
      will(returnValue(Optional.absent()));

    }});

    Response response = identityAuthorizationActivity.execute(anyAuth, anyClient, request, anyInstantTime);

    assertThat(new RsPrint(response).printBody(), containsString("identity was not found"));
  }

  @Test
  public void identityParamsArePassed() throws Exception {
    final Client anyClient = aNewClient().build();
    final DateTime anyInstantTime = new DateTime();
    final Authorization anyAuth = newAuthorization().addParam("index", "1").build();

    context.checking(new Expectations() {{
      oneOf(identityFinder).findIdentity(new FindIdentityRequest(anyAuth.identityId, GrantType.AUTHORIZATION_CODE, anyInstantTime, anyAuth.params, anyAuth.clientId));
      will(returnValue(Optional.absent()));
    }});

    identityAuthorizationActivity.execute(anyAuth, anyClient, aNewRequest().param("code", "::any code::").build(), anyInstantTime);
  }
}