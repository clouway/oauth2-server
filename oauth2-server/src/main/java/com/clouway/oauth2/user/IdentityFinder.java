package com.clouway.oauth2.user;

import com.clouway.oauth2.http.Request;
import com.google.common.base.Optional;

/**
 * IdentityFinder is finding the Identity of the request.
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface IdentityFinder {
  /**
   * Finds Identity of the caller.
   *
   * @param request the request from which identity will be retrieved
   * @return an optional value with the identity id if it's associated with the provided request or absent value if not
   */
  Optional<String> find(Request request);
}
