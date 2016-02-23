package com.clouway.oauth2.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Request is representing a HTTP request.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface Request {
  /**
   * Get request path.
   *
   * @return the requested path
   */
  String path();

  /**
   * Get parameter value by it's key.
   *
   * @param name the name of the parameter
   * @return the parameter value
   */
  String param(String name);

  /**
   * Get all parameter names.
   *
   * @return all names
   */
  Iterable<String> names();

  /**
   * Get single cookie.
   *
   * @param name Cookie name
   * @return list of values (can be empty)
   */
  Iterable<String> cookie(String name);

  /**
   * Get header value by it's key.
   *
   * @param name the name of the header
   * @return the value that is associated with provided name
   */
  String header(String name);

  /**
   * Get body of the request.
   *
   * @return an input stream of the body of the request
   * @throws IOException is thrown when body cannot be read due IO error
   */
  InputStream body() throws IOException;

}
