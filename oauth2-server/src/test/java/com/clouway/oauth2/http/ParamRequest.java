package com.clouway.oauth2.http;

import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * ParamRequest is a simple request that is only passing parameter values.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class ParamRequest implements Request {
  private final Map<String, String> params;

  public ParamRequest(Map<String, String> params) {
    this.params = params;
  }

  @Override
  public String path() {
    return "/";
  }

  @Override
  public String param(String name) {
    return params.get(name);
  }

  @Override
  public Iterable<String> names() {
    return params.keySet();
  }

  @Override
  public Iterable<String> cookie(String name) {
    return Lists.newLinkedList();
  }

  @Override
  public String header(String name) {
    return null;
  }

  @Override
  public InputStream body() throws IOException {
    return new ByteArrayInputStream(new byte[]{});
  }
}
