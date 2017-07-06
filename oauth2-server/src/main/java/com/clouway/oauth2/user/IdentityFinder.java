package com.clouway.oauth2.user;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.Identity;
import com.clouway.oauth2.token.GrantType;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * IdentityFinder is finding the Identity of the request.
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface IdentityFinder {

  /**
   * Finds identity of the resource provider by providing it's id and the GrantType that was requested.
   *
   * @param identityId  the identityId of the Resource Owner.
   * @param grantType   the grant type that was acknowledged
   * @param instantTime the time on which it was requested
   * @param params
   * @return the associated identity by that id or absent value if it's not available.
   */
  Optional<Identity> findIdentity(String identityId, GrantType grantType, DateTime instantTime, Map<String, String> params);
}
