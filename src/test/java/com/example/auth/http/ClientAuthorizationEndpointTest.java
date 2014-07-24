package com.example.auth.http;

import com.example.auth.core.AuthorizationErrorResponse;
import com.example.auth.core.AuthorizationRequest;
import com.example.auth.core.AuthorizationResponse;
import com.example.auth.core.authorization.AuthorizationSecurity;
import com.google.common.collect.ImmutableMap;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static com.example.auth.http.ParameterRequest.makeRequestWithParameters;
import static com.example.auth.http.ReplyMatchers.contains;
import static com.example.auth.http.ReplyMatchers.isBadRequest;
import static com.example.auth.http.ReplyMatchers.redirectUriIs;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class ClientAuthorizationEndpointTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private AuthorizationSecurity authorizationSecurity = context.mock(AuthorizationSecurity.class);

  private AuthorizationEndpoint endpoint = new AuthorizationEndpoint(authorizationSecurity);

  @Test
  public void happyPath() throws Exception {
    final Request request = makeRequestWithParameters(ImmutableMap.of("response_type", "code", "client_id", "123456"));
    final AuthorizationRequest authorizationRequest = new AuthorizationRequest("code", "123456");
    final AuthorizationResponse authorizationResponse = new AuthorizationResponse("54321", "http://abv.bg/");

    context.checking(new Expectations() {{
      oneOf(authorizationSecurity).auth(authorizationRequest);
      will(returnValue(authorizationResponse));
    }});

    Reply<?> reply = endpoint.authorize(request);
    assertThat(reply, redirectUriIs("http://abv.bg/?code=54321"));
  }

  @Test
  public void onAuthorizationError() throws Exception {
    final Request request = makeRequestWithParameters(ImmutableMap.of("response_type", "code", "client_id", "654321"));
    final AuthorizationRequest authorizationRequest = new AuthorizationRequest("code", "654321");

    context.checking(new Expectations() {{
      oneOf(authorizationSecurity).auth(authorizationRequest);
      will(throwException(new AuthorizationErrorResponse("Error messageeeeee!")));
    }});

    Reply<?> reply = endpoint.authorize(request);

    assertThat(reply, isBadRequest());
    assertThat(reply, contains("Error messageeeeee!"));
  }
}