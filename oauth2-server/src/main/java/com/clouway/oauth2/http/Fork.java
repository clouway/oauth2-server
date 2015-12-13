package com.clouway.oauth2.http;

import com.google.common.base.Optional;

import java.io.IOException;

/**
 * Fork.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface Fork {

  /**
   * Routes the provided request and returns response.
   *
   * @param request the request to be routed
   * @return an optional response
   */
  Optional<Response> route(Request request) throws IOException;
}
