package com.clouway.oauth2.http;

import com.google.common.base.Optional;

import java.io.IOException;

/**
 * RequestHandlerMatchingParams is an optional handler which could handle request only if the requested param is matching
 * the provided value.
 * <p/>
 *
 * An absent value is returned when parameter value is not matching.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RequestHandlerMatchingParams implements Fork {
  private final String key;
  private final String value;
  private final Take take;

  public RequestHandlerMatchingParams(String key, String value, Take take) {
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
