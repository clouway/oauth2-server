package com.example.auth.http;

import com.example.auth.core.ResourceOwnerAuthentication;
import com.example.auth.core.Session;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.Cookie;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class LoginEndpointTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ResourceOwnerAuthentication authentication = context.mock(ResourceOwnerAuthentication.class);
  private FakeHttpServletResponse response = new FakeHttpServletResponse();

  private LoginEndpoint endpoint = new LoginEndpoint(authentication);

  @Test
  public void happyPath() throws Exception {
    final Session session = new Session("session_value");
    final Cookie cookie = new Cookie("session_id", session.value);

    context.checking(new Expectations() {{
      oneOf(authentication).auth("FeNoMeNa", "parola");
      will(returnValue(Optional.of(session)));
    }});

    insertUserData("FeNoMeNa", "parola", "/movies");

    String redirectPage = endpoint.login(response);

    response.assertContains(cookie);
    assertThat(redirectPage, is("/movies"));
  }

  @Test
  public void wrongAuthentication() throws Exception {
    context.checking(new Expectations() {{
      oneOf(authentication).auth("Grigor", "dimitrov");
      will(returnValue(Optional.absent()));
    }});

    insertUserData("Grigor", "dimitrov", "/xxx");

    String redirectPage = endpoint.login(response);

    response.assertDoesNotExist("session_id");
    assertThat(redirectPage, is("/xxx"));
  }

  private void insertUserData(String username, String password, String redirectPage) {
    endpoint.setUsername(username);
    endpoint.setPassword(password);
    endpoint.setPage(redirectPage);
  }
}