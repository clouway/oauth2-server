package com.clouway.oauth2.http;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class TkRequestWrap implements Request {
  private final transient HttpServletRequest req;

  public TkRequestWrap(HttpServletRequest req) {
    this.req = req;
  }

  @Override
  public String path() {
    return req.getRequestURI();
  }

  @Override
  public String param(String name) {
    return req.getParameter(name);
  }

  @Override
  public String header(String name) {
    return req.getHeader(name);
  }

  @Override
  public InputStream body() throws IOException {
    return req.getInputStream();
  }
}
