package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.Status;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.authorization.AuthorizationRequest;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.oauth2.codechallenge.CodeChallenge;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;

import static com.clouway.friendlyserve.testing.FakeRequest.aNewRequest;
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
  ClientFinder clientFinder;
  private ClientAuthorizationActivity activity;

  @Before
  public void setUpActivity() {
    activity = new ClientAuthorizationActivity(clientFinder, clientAuthorizationRepository);
  }

  private final CodeChallenge emptyCodeChallenge = new CodeChallenge("", "");

  @Test
  public void happyPath() throws IOException {
    Request authRequest = aNewRequest().params(
            ImmutableMap.of("response_type", "code", "client_id", "::client_id::", "redirect_uri", "http://example.com/callback", "customerIndex", "::customerIndex::")
    ).build();
    final Client anyExistingClient = aNewClient().withRedirectUrl("http://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient("::client_id::");
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(clientAuthorizationRepository).authorize(new AuthorizationRequest(anyExistingClient, "user1", "code", Collections.<String>emptySet(), emptyCodeChallenge, ImmutableMap.of("customerIndex", "::customerIndex::")));
      will(returnValue(Optional.of(new Authorization("code", "::client_id::", "identityId", "1234", Collections.<String>emptySet(), Collections.singleton("http://example.com/callback"), ImmutableMap.of("customerIndex", "::customerIndex::")))));
    }});

    Response response = activity.execute("user1", authRequest);
    Status status = response.status();

    assertThat(status.code, is(HttpURLConnection.HTTP_MOVED_TEMP));
    assertThat(status.redirectUrl, is(containsString("http://example.com/callback?code=1234")));
  }


  @Test
  @SuppressWarnings("unchecked")
  public void stateIsPassedToCallbackWhenProvided() throws Exception {
    Request authRequest = aNewRequest().params(
            ImmutableMap.of(
                    "response_type", "code",
                    "client_id", "::client_id::",
                    "redirect_uri", "http://example.com/callback",
                    "state", "abc",
                    "customer", "::customerIndex::"
            )
    ).build();
    final Client anyExistingClient = aNewClient().withRedirectUrl("http://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient(with(any(String.class)));
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(clientAuthorizationRepository).authorize(with(any(AuthorizationRequest.class)));
      will(returnValue(Optional.of(new Authorization("code", "::client_id::", "identityId", "1234", Collections.<String>emptySet(), Collections.singleton("http://example.com/callback"), Collections.singletonMap("customer", "::customerIndex::")))));
    }});

    Response response = activity.execute("user1", authRequest);
    Status status = response.status();

    assertThat(status.code, is(HttpURLConnection.HTTP_MOVED_TEMP));
    assertThat(status.redirectUrl, is(containsString("http://example.com/callback?code=1234&state=abc")));
  }

  @Test
  public void singleScopeIsPassed() throws Exception {
    Request authRequest = aNewRequest().params(
            ImmutableMap.of(
                    "response_type", "code",
                    "client_id", "::client_id::",
                    "redirect_uri", "http://example.com/callback",
                    "scope", "abc",
                    "customerIndex", "::customerIndex::"
            )
    ).build();
    final Client anyExistingClient = aNewClient().withRedirectUrl("http://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient(with(any(String.class)));
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(clientAuthorizationRepository).authorize(new AuthorizationRequest(anyExistingClient, "user1", "code", Collections.singleton("abc"), emptyCodeChallenge, ImmutableMap.of("customerIndex", "::customerIndex::")));
      will(returnValue(Optional.of(new Authorization("code", "::client_id::", "identityId", "1234", Collections.<String>emptySet(), Collections.singleton("http://example.com/callback"), ImmutableMap.of("customerIndex", "::customerIndex::")))));
    }});

    activity.execute("user1", authRequest);
  }

  @Test
  public void multipleScopesArePassed() throws Exception {
    Request authRequest = aNewRequest().params(
            ImmutableMap.of(
                    "response_type", "code",
                    "client_id", "::client_id::",
                    "redirect_uri", "http://example.com/callback",
                    "scope", "CanDoX CanDoY CanDoZ",
                    "customerIndex", "::customerIndex::"
            )
    ).build();
    final Client anyExistingClient = aNewClient().withRedirectUrl("http://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient(with(any(String.class)));
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(clientAuthorizationRepository).authorize(new AuthorizationRequest(anyExistingClient, "user1", "code", Sets.newTreeSet(Arrays.asList("CanDoX", "CanDoY", "CanDoZ")), emptyCodeChallenge, ImmutableMap.of("customerIndex", "::customerIndex::")));
      will(returnValue(Optional.of(new Authorization("code", "::client_id::", "identityId", "1234", Collections.<String>emptySet(), Collections.singleton("http://example.com/callback"), ImmutableMap.of("customerIndex", "::customerIndex::")))));
    }});

    activity.execute("user1", authRequest);
  }

  @Test
  public void redirectUrlWasNotRequested() throws Exception {
    Request authRequest = aNewRequest().params(
            ImmutableMap.of("response_type", "code", "client_id", "::another client::", "customerIndex", "::customerIndex::")
    ).build();
    final Client anyExistingClient = aNewClient().withRedirectUrl("http://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient(with(any(String.class)));
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(clientAuthorizationRepository).authorize(new AuthorizationRequest(anyExistingClient, "user1", "code", Collections.<String>emptySet(), emptyCodeChallenge, ImmutableMap.of("customerIndex", "::customerIndex::" +
              "")));
      will(returnValue(Optional.of(new Authorization("code", "::client_id::", "identityId", "1234", Collections.<String>emptySet(), Collections.singleton("http://example.com/callback"), ImmutableMap.of("customerIndex", "::customerIndex::")))));
    }});

    Response response = activity.execute("user1", authRequest);
    Status status = response.status();

    assertThat(status.code, is(HttpURLConnection.HTTP_MOVED_TEMP));
    assertThat(status.redirectUrl, is(containsString("http://example.com/callback?code=1234")));
  }

  @Test
  public void clientWasNotAuthorized() {
    Request authRequest = aNewRequest().params(
            ImmutableMap.of("response_type", "code", "client_id", "::client_id::", "redirect_uri", "https://example.com/callback", "customerIndex", "::customerIndex::")
    ).build();
    final Client anyExistingClient = aNewClient().withRedirectUrl("https://example.com/callback").build();

    context.checking(new Expectations() {{

      oneOf(clientFinder).findClient("::client_id::");
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(clientAuthorizationRepository).authorize(new AuthorizationRequest(anyExistingClient, "::identity_id::", "code", Collections.<String>emptySet(), emptyCodeChallenge, ImmutableMap.of("customerIndex", "::customerIndex::")));
      will(returnValue(Optional.absent()));
    }});

    Response response = activity.execute("::identity_id::", authRequest);
    Status status = response.status();

    assertThat(status.code, is(HttpURLConnection.HTTP_MOVED_TEMP));
    assertThat(status.redirectUrl, is(equalTo("https://example.com/callback?error=access_denied")));
  }

  @Test
  public void unknownClient() {
    Request authRequest = aNewRequest().params(
            ImmutableMap.of("response_type", "code", "client_id", "::client_id::", "redirect_uri", "https://example.com")
    ).build();
    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient("::client_id::");
      will(returnValue(Optional.absent()));
    }});

    Response response = activity.execute("::any_identity_id::", authRequest);
    Status status = response.status();
    assertThat(status.code, is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void clientRedirectUrlIsNotMatching() throws Exception {
    Request authRequest = aNewRequest().params(
            ImmutableMap.of(
                    "response_type", "code",
                    "client_id", "::client_id::",
                    "redirect_uri", "https://example.com"
            )
    ).build();

    final Client anyExistingClient = aNewClient().withRedirectUrl("https://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient("::client_id::");
      will(returnValue(Optional.of(anyExistingClient)));
    }});

    Response response = activity.execute("::identity_id::", authRequest);
    Status status = response.status();
    assertThat(status.code, is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void authorizeWithCodeChallenge() throws Exception {
    Request authRequest = aNewRequest().params(
            ImmutableMap.of("response_type", "code", "client_id", "::client_id::", "redirect_uri", "http://example.com/callback", "code_challenge", "::code_challenge::", "code_challenge_method", "::chosenMethod::")
    ).build();
    final Client anyExistingClient = aNewClient().withRedirectUrl("http://example.com/callback").build();

    context.checking(new Expectations() {{
      oneOf(clientFinder).findClient("::client_id::");
      will(returnValue(Optional.of(anyExistingClient)));

      oneOf(clientAuthorizationRepository).authorize(new AuthorizationRequest(anyExistingClient, "user1", "code", Collections.<String>emptySet(), new CodeChallenge("::code_challenge::", "::chosenMethod::"), Collections.<String, String>emptyMap()));
      will(returnValue(Optional.of(new Authorization("code", "::client_id::", "identityId", "1234", Collections.<String>emptySet(), Collections.singleton("http://example.com/callback"), new CodeChallenge("::code_challenge::", "::chosenMethod::"), Collections.<String, String>emptyMap()))));
    }});

    activity.execute("user1", authRequest);
  }
}