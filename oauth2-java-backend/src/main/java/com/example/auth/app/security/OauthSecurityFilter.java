package com.example.auth.app.security;

import com.example.auth.core.session.Session;
import com.example.auth.core.session.SessionSecurity;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Singleton
public class OauthSecurityFilter implements Filter {
  private final SessionSecurity sessionSecurity;
  private final SecuredResources securedResources;
  private String uriPath;
  private String loginPagePath;

  @Inject
  public OauthSecurityFilter(SessionSecurity sessionSecurity, SecuredResources securedResources, @UriPath String uriPath, @LoginPageUrl String loginPagePath) {
    this.sessionSecurity = sessionSecurity;
    this.securedResources = securedResources;
    this.uriPath = uriPath;
    this.loginPagePath = loginPagePath;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    if (securedResources.contains(request.getRequestURI())) {
      //"SID" - this value should be configurable
      Session session = new Session(getCookieValue(request, "SID"));

      if (!sessionSecurity.exists(session)) {

        String path = uriPath + "/login";

        if (!Strings.isNullOrEmpty(loginPagePath)) {
          path = loginPagePath;
        }

        String location = path + "?redirectUrl=" + getRedirectURI(request);

        response.sendRedirect(location);
        return;
      }
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {

  }

  /**
   * Find and returns the value of the cookie with the provided name
   *
   * @param request http request from which cookie will be extracted
   * @param name    cookie name
   * @return the value of the cookie if found null otherwise
   */
  private String getCookieValue(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null) { //Stupid servlet API!
      for (Cookie cookie : cookies) {
        if (name.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }

    return "";
  }

  private String getRedirectURI(HttpServletRequest request) throws UnsupportedEncodingException {
    String query = (request.getQueryString() == null) ? "" : "?" + request.getQueryString();

    return URLEncoder.encode(request.getRequestURI() + query, "UTF-8");
  }
}