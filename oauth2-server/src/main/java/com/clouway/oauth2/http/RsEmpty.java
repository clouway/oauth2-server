package com.clouway.oauth2.http;

import com.google.common.collect.ImmutableMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * RsEmpty is a an empty response without headers and body.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsEmpty implements Response {

  @Override
  public Map<String, String> header() throws IOException {
    return ImmutableMap.of();
  }

  @Override
  public InputStream body() throws IOException {
    return new ByteArrayInputStream(new byte[0]);
  }
}
