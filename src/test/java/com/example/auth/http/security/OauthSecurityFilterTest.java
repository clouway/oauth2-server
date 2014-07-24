package com.example.auth.http.security;

import com.example.auth.core.Session;
import com.example.auth.core.SessionSecurity;
import com.example.auth.http.FakeHttpServletRequest;
import com.example.auth.http.FakeHttpServletResponse;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class OauthSecurityFilterTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private SessionSecurity sessionSecurity = context.mock(SessionSecurity.class);
  private FilterChain chain = context.mock(FilterChain.class);

  private SecuredResources securedResources = createSecuredResources("/auth", "/xxx", "movies");

  private OauthSecurityFilter filter = new OauthSecurityFilter(sessionSecurity, securedResources,"");

  @Test
  public void happyPath() throws Exception {
    final Session session = new Session("token123");
    final FakeHttpServletRequest servletRequest = new FakeHttpServletRequest("/auth", authCookie(session.value));
    final FakeHttpServletResponse servletResponse = new FakeHttpServletResponse();

    context.checking(new Expectations() {{
      oneOf(sessionSecurity).exists(session);
      will(returnValue(true));

      oneOf(chain).doFilter(servletRequest, servletResponse);
    }});

    filter.doFilter(servletRequest, servletResponse, chain);
  }

  @Test
  public void invalidSession() throws Exception {
    final Session session = new Session("123token");
    final FakeHttpServletRequest servletRequest = new FakeHttpServletRequest("/xxx?redirect_uri=http://abv.bg/", authCookie(session.value));
    final FakeHttpServletResponse servletResponse = new FakeHttpServletResponse();

    context.checking(new Expectations() {{
      oneOf(sessionSecurity).exists(session);
      will(returnValue(false));
    }});

    filter.doFilter(servletRequest, servletResponse, chain);

    servletResponse.assertRedirectTo("/login?page=%2Fxxx%3Fredirect_uri%3Dhttp%3A%2F%2Fabv.bg%2F");
  }

  @Test
  public void unsecuredResource() throws Exception {
    final FakeHttpServletRequest servletRequest = new FakeHttpServletRequest("/login", authCookie("token"));
    final FakeHttpServletResponse servletResponse = new FakeHttpServletResponse();

    context.checking(new Expectations() {{
      oneOf(chain).doFilter(servletRequest, servletResponse);
    }});

    filter.doFilter(servletRequest, servletResponse, chain);
  }

  private SecuredResources createSecuredResources(final String... resources) {
    SecuredResources securedResources = new SecuredResources();

    for (String resource : resources) {
      securedResources.add(resource);
    }

    return securedResources;
  }

  private Cookie[] authCookie(String value) {
    return new Cookie[] {new Cookie("session_id", value)};
  }
}