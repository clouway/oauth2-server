package com.clouway.oauth2.http;

import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * RequiresHeader is a decorator class over {@link Take} that checks whether the provided header is passed to the request
 * or not. If it's not passed then bad request is returned as response otherwise request is passed to provided origin.
 * <p/>
 * This class is use-full as validation clause to verify that certain header is passed.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RequiresHeader implements Take {
  private final String headerName;
  private final Take origin;

  public RequiresHeader(String headerName, Take origin) {
    this.headerName = headerName;
    this.origin = origin;
  }

  @Override
  public Response ack(Request request) throws IOException {
    String headerValue = request.header(headerName);
    if (isNullOrEmpty(headerValue)) {
      return new RsBadRequest();
    }
    return origin.ack(request);
  }
}
