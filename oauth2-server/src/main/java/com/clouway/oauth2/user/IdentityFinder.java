package com.clouway.oauth2.user;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.Identity;
import com.clouway.friendlyserve.Request;
import com.clouway.oauth2.token.GrantType;
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
   * @param instantTime the time on which request was executed
   * @return an optional value with the identity id if it's associated with the provided request or absent value if not
   */
  Optional<String> find(Request request, DateTime instantTime);

  Optional<Identity> findIdentity(String identityId, GrantType grantType, DateTime instantTime);
}
