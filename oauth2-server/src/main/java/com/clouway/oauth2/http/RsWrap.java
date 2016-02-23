package com.clouway.oauth2.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsWrap implements Response {

  /**
   * Original response.
   */
  private final transient Response origin;

  public RsWrap(Response origin) {
    this.origin = origin;
  }

  @Override
  public Status status() {
    return origin.status();
  }

  @Override
  public final Map<String, String> header() throws IOException {
    return origin.header();
  }

  @Override
  public final InputStream body() throws IOException {
    return origin.body();
  }
}
