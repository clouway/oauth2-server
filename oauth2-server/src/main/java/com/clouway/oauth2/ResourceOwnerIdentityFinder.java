package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.oauth2.common.DateTime;
import com.google.common.base.Optional;

/**
 * ResourceOwnerIdentityFinder is a finder which is used during the authorization of the request.
 * <p/>
 * <p>
 * In most cases implementations of this class should ask the session store for retrieving of the identity which is
 * assicated with the request.
 * <p/>
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface ResourceOwnerIdentityFinder {

  /**
   * Finds Identity of the caller.
   * <p/>
   * In most cases the request will contain a Cookie value which will be used
   * as lookup key for the session of the Owner which contains and the requested identity.
   *
   * @param request     the request from which identity will be retrieved
   * @param instantTime the time on which request was executed
   * @return an optional value with the identity id if it's associated with the provided request or absent value if not
   */
  Optional<String> find(Request request, DateTime instantTime);
}
