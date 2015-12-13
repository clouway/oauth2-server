package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;

/**
 * TokenController is a controller which is responsible for all token related stuff.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
interface TokenRequestHandler {

  /**
   * Handles token request and writes response to the provided responseWriter.
   * @param responseWriter the responseWriter used for writing of response
   * @param request the token request
   */
  void handle(ResponseWriter responseWriter, Request request);

}
