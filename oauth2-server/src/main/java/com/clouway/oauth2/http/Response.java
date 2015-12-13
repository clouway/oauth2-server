package com.clouway.oauth2.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Response is representing the response of HTTP request.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface Response {

  /**
   * HTTP response header.
   *
   * @return map of HTTP header pairs
   * @throws IOException If something goes wrong
   */
  Map<String, String> header() throws IOException;

  /**
   * HTTP response body.
   *
   * @return Stream with body
   * @throws IOException If something goes wrong
   */
  InputStream body() throws IOException;
}
