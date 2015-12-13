package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.google.common.collect.ImmutableMap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class ByteRequest implements Request {

  private final Map<String, String> params;
  private final Map<String, String> headers;
  private final byte[] body;
  private final String path;

  public ByteRequest(String path, Map<String, String> params, Map<String, String> headers, byte[] content) {
    this.path = path;
    this.params = ImmutableMap.copyOf(params);
    this.body = Arrays.copyOf(content, content.length);
    this.headers = ImmutableMap.copyOf(headers);
  }

  public ByteRequest(Map<String, String> params, Map<String, String> headers) {
    this("/", params, headers, new byte[]{});
  }

  public ByteRequest(String path, Map<String, String> params, byte[] content) {
    this(path, params, new LinkedHashMap<String, String>(), content);
  }


  @Override
  public String path() {
    return path;
  }

  @Override
  public String param(String name) {
    return params.get(name);
  }

  @Override
  public String header(String name) {
    return headers.get(name);
  }

  @Override
  public InputStream body() {
    return new ByteArrayInputStream(body);
  }
}
