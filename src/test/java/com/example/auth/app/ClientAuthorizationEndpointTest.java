package com.example.auth.app;

import com.example.auth.core.authorization.AuthorizationErrorResponse;
import com.example.auth.core.authorization.AuthorizationRequest;
import com.example.auth.core.authorization.AuthorizationResponse;
import com.example.auth.core.authorization.AuthorizationSecurity;
import com.google.common.collect.Lists;
import com.google.sitebricks.headless.Reply;
import org.hamcrest.MatcherAssert;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class ClientAuthorizationEndpointTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private AuthorizationSecurity authorizationSecurity = context.mock(AuthorizationSecurity.class);

  private AuthorizationEndpoint endpoint = new AuthorizationEndpoint(authorizationSecurity);

  @Mock
  private HttpServletRequest request;
  private Cookie[] coockies = new Cookie[1];


  @Before
  public void setUp() throws Exception {
    Lists.newArrayList(new Cookie("session_id", "xxxyyyzzz")).toArray(coockies);
  }

  @Test
  public void happyPath() throws Exception {
//    final Request request = makeRequestWithParameters(ImmutableMap.of("response_type", "code", "client_id", "123456"));
    final AuthorizationRequest authorizationRequest = new AuthorizationRequest("code", "123456", "xxxyyyzzz");
    final AuthorizationResponse authorizationResponse = new AuthorizationResponse("54321", "http://abv.bg/");

    context.checking(new Expectations() {{
      oneOf(request).getParameter("response_type");
      will(returnValue("code"));
      oneOf(request).getParameter("client_id");
      will(returnValue("123456"));
      oneOf(request).getCookies();
      will(returnValue(coockies));

      oneOf(authorizationSecurity).auth(authorizationRequest);
      will(returnValue(authorizationResponse));
    }});

    Reply<?> reply = endpoint.authorize(request);
    MatcherAssert.assertThat(reply, ReplyMatchers.redirectUriIs("http://abv.bg/?code=54321"));
  }

  @Test
  public void onAuthorizationError() throws Exception {
//    final Request request = makeRequestWithParameters(ImmutableMap.of("response_type", "code", "client_id", "654321"));
    final AuthorizationRequest authorizationRequest = new AuthorizationRequest("code", "654321", "xxxyyyzzz");

    context.checking(new Expectations() {{

      oneOf(request).getParameter("response_type");
      will(returnValue("code"));
      oneOf(request).getParameter("client_id");
      will(returnValue("654321"));
      oneOf(request).getCookies();
      will(returnValue(coockies));


      oneOf(authorizationSecurity).auth(authorizationRequest);
      will(throwException(new AuthorizationErrorResponse("Error messageeeeee!")));
    }});

    Reply<?> reply = endpoint.authorize(request);

    MatcherAssert.assertThat(reply, ReplyMatchers.isBadRequest());
    MatcherAssert.assertThat(reply, ReplyMatchers.contains("Error messageeeeee!"));
  }
}