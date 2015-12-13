package com.clouway.oauth2.http;

import com.google.common.base.Optional;

import java.io.IOException;

/**
 * Fork by query params and there values, matched by simple comparison.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class FkParams implements Fork {
  private final String key;
  private final String value;
  private final Take take;

  public FkParams(String key, String value, Take take) {
    this.key = key;
    this.value = value;
    this.take = take;
  }

  @Override
  public Optional<Response> route(Request request) throws IOException {
    String param = request.param(key);
    if (value.equals(param)) {
      return Optional.of(take.ack(request));
    }

    return Optional.absent();
  }
}
