package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.Session;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.sitebricks.Show;
import com.google.sitebricks.http.Post;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Show("login.html")
public class LoginEndpoint {
  private String username;
  private String password;
  private String errormessage;

  private ResourceOwnerAuthentication authentication;

  @Inject
  public LoginEndpoint(ResourceOwnerAuthentication authentication) {
    this.authentication = authentication;
  }
  
  @Post
  public String login(HttpServletRequest request, HttpServletResponse response) {
    Optional<Session> session = authentication.auth(username, password, request.getRemoteAddr());
    String continueUrl = request.getParameter("continue");

    if (session.isPresent()) {
      // "SID" -this value should be configurable or in the
      // login should be responsible for that hardcoded string
      Cookie sid = new Cookie("SID", session.get().value);
      sid.setPath("/");

      response.addCookie(sid);
      
      return continueUrl;
    } else {
        try {
			return request.getRequestURI() +					
					"?continue="+URLEncoder.encode(continueUrl,"UTF-8")+
				  "&errormessage="+URLEncoder.encode("Invalid username or password","UTF-8");
		} catch (UnsupportedEncodingException e) {
			return request.getRequestURI() +
					"?continue="+URLEncoder.encode(continueUrl)+
					  "&errormessage="+URLEncoder.encode("Invalid username or password");
		}
    }
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getErrormessage() {
	return errormessage;
  }

  public void setErrormessage(String errormessage) {
	this.errormessage = errormessage;
  }
}