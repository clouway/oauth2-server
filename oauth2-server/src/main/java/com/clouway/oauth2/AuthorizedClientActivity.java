package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.client.Client;

import java.util.Set;

/**
 * AuthorizedClientActivity is an activity which is represents the authorization client.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface AuthorizedClientActivity {

  /**
   * Handles client request of authorized client and returns response.
   *
   * @param client  the client of which request will be handled
   * @param request the request
   * @param instant the time of which client was requested access
   * @return the response
   */
  Response execute(Client client, Identity identity, Set<String> scopes, Request request, DateTime instant);
}
