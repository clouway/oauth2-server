package com.clouway.oauth2.http;

import com.google.common.collect.Lists;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

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
  public Iterable<String> names() {
    return req.getParameterMap().keySet();
  }

  @Override
  public Iterable<String> cookie(String name) {
    Cookie[] cookies = req.getCookies();
    if (cookies == null) {
      return new LinkedList<String>();
    }

    List<String> values = Lists.newLinkedList();
    for (Cookie each : cookies) {
      if (name.equalsIgnoreCase(each.getName())) {
        values.add(each.getValue());
      }
    }
    return values;
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
