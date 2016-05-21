package com.clouway.oauth2.http;

import com.google.common.base.Optional;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * RequiresHeader is a decorator class over {@link Fork} that checks whether the provided header is passed to the request
 * or not. If it's not passed then routing is terminated and absent value is returned.
 * <p/>
 * This class is use-full as validation clause to verify that certain header is passed.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RequiresHeader implements Fork {
  private final String headerName;
  private final Fork origin;

  public RequiresHeader(String headerName, Fork origin) {
    this.headerName = headerName;
    this.origin = origin;
  }

  @Override
  public Optional<Response> route(Request request) throws IOException {

    String headerValue = request.header(headerName);
    if (isNullOrEmpty(headerValue)) {
      throw new HttpException(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    return origin.route(request);
  }
}
