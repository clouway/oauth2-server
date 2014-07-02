package com.example.auth.http;

import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
@Service
@At("/authorize")
public class AuthorizationEndpoint {
  @Get
  public Reply authorize(Request request) {
    return Reply.saying().ok();
  }
}