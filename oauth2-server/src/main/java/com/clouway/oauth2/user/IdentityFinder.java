package com.clouway.oauth2.user;

import com.clouway.oauth2.Identity;
import com.google.common.base.Optional;

/**
 * IdentityFinder is finding the Identity of the request.
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface IdentityFinder {

  /**
   * Finds identity of the resource provider by providing it's id and the GrantType that was requested.
   *
   * @param request  find identity request
   * @return the associated identity by that id or absent value if it's not available.
   */
  Optional<Identity> findIdentity(FindIdentityRequest request);
}
