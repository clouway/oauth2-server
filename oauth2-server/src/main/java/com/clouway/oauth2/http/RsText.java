package com.clouway.oauth2.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsText implements Response {
  private final String body;

  public RsText(String body) {
    this.body = body;
  }

  @Override
  public Map<String, String> header() throws IOException {
    return Collections.emptyMap();
  }

  @Override
  public InputStream body() throws IOException {
    return new ByteArrayInputStream(body.getBytes());
  }
}
