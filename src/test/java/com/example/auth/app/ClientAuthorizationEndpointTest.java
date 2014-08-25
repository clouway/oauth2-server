package com.example.auth.app;

import com.example.auth.core.authorization.AuthorizationErrorResponse;
import com.example.auth.core.authorization.AuthorizationRequest;
import com.example.auth.core.authorization.AuthorizationResponse;
import com.example.auth.core.authorization.AuthorizationSecurity;
import com.google.common.collect.ImmutableMap;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import org.hamcrest.MatcherAssert;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static com.example.auth.app.ParameterRequest.makeRequestWithParameters;

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
    MatcherAssert.assertThat(reply, ReplyMatchers.redirectUriIs("http://abv.bg/?code=54321"));
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

    MatcherAssert.assertThat(reply, ReplyMatchers.isBadRequest());
    MatcherAssert.assertThat(reply, ReplyMatchers.contains("Error messageeeeee!"));
  }
}