package com.example.auth.http;

import com.example.auth.core.ResourceOwnerAuthentication;
import com.example.auth.core.Session;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.http.Post;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Show("login.html")
@At("/login")
public class LoginEndpoint {
  private String username;
  private String password;
  private String page;

  private ResourceOwnerAuthentication authentication;

  @Inject
  public LoginEndpoint(ResourceOwnerAuthentication authentication) {
    this.authentication = authentication;
  }

  @Post
  public String login(HttpServletResponse response) {
    Optional<Session> session = authentication.auth(username, password);

    if (session.isPresent()) {
      response.addCookie(new Cookie("session_id", session.get().value));
    }

    return page;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPage(String page) {
    this.page = page;
  }
}