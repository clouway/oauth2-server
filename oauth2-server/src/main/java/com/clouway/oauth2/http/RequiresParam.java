package com.clouway.oauth2.http;

import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * RequiresParam is a decorating class over {@link Fork} which checks whether the provided param is passed with the request.
 * <p/>
 * <p>
 * In cases when param is not passed, the route method is throwing {@link HttpException} with BAD_REQUEST value to indicate
 * that some of the params is missing.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RequiresParam implements Take {

  private final String param;
  private final Take origin;

  public RequiresParam(String param, Take origin) {
    this.param = param;
    this.origin = origin;
  }

  @Override
  public Response ack(Request request) throws IOException {

    String paramValue = request.param(this.param);

    if (isNullOrEmpty(paramValue)) {
      return new RsBadRequest();
    }

    return origin.ack(request);
  }
}
