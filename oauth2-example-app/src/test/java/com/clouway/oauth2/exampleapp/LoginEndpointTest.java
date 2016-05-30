package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.Session;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class LoginEndpointTest {
  private final String  remoteAddress = "123.2.2.2";
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ResourceOwnerAuthentication authentication = context.mock(ResourceOwnerAuthentication.class);
  private FakeHttpServletResponse response = new FakeHttpServletResponse();

  private LoginEndpoint endpoint = new LoginEndpoint(authentication);

  @Mock
  HttpServletRequest request;

  @Test
  public void happyPath() throws Exception {
    final Session session = new Session("session_value");
    final Cookie cookie = new Cookie("SID", session.value);

    context.checking(new Expectations() {{
      oneOf(request).getRemoteAddr();
      will(Expectations.returnValue(remoteAddress));
      oneOf(request).getParameter("continue");
      will(returnValue("/movies"));
      oneOf(authentication).auth("FeNoMeNa", "parola", remoteAddress);
      will(Expectations.returnValue(Optional.of(session)));
    }});

    pretendThatUserIsAuthorisedWith("FeNoMeNa", "parola");

    String redirectPage = endpoint.login(request,response);

    response.assertContains(cookie);
    assertThat(redirectPage, is("/movies"));
  }

  @Test
  public void wrongAuthentication() throws Exception {
    context.checking(new Expectations() {{
      oneOf(request).getRemoteAddr();
      will(Expectations.returnValue(remoteAddress));
      oneOf(request).getParameter("continue");
      will(returnValue("/xxx"));
      oneOf(authentication).auth("Grigor", "dimitrov", remoteAddress);
      will(Expectations.returnValue(Optional.absent()));
    }});

    pretendThatUserIsAuthorisedWith("Grigor", "dimitrov");

    String redirectPage = endpoint.login(request, response);

    response.assertDoesNotExist("SID");
    assertThat(redirectPage, is("/xxx"));
  }

  private void pretendThatUserIsAuthorisedWith(String username, String password) {
    endpoint.setUsername(username);
    endpoint.setPassword(password);
  }
}