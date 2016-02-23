package com.clouway.oauth2.http;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsWithHeader extends RsWrap {

  public RsWithHeader(String key, String value) {
    this(new RsEmpty(), key, value);
  }

  public RsWithHeader(final Response res, final String key, final String value) {
    super(new Response() {
      @Override
      public Status status() {
        return new Status(HttpURLConnection.HTTP_OK);
      }

      @Override
      public Map<String, String> header() throws IOException {
        return ImmutableMap.<String, String>builder().putAll(res.header()).put(key, value).build();
      }

      @Override
      public InputStream body() throws IOException {
        return res.body();
      }
    });
  }
}
