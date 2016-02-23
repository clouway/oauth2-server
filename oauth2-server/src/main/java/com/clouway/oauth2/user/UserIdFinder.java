package com.clouway.oauth2.user;

import com.clouway.oauth2.http.Request;
import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface UserIdFinder {
  /**
   * Finds associated user with the provided request.
   *
   * @param request the request from which user anchor will be retrieved
   * @return an optional value with the id of the user if it's associated with the provided request or absent value if not
   */
  Optional<String> find(Request request);
}
