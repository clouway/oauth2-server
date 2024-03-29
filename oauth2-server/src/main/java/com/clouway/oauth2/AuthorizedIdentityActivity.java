package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.token.Identity;

import java.util.Map;
import java.util.Set;

/**
 * AuthorizedIdentityActivity is an activity which is represents the authorization client.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
interface AuthorizedIdentityActivity {

  /**
   * Handles client request of authorized client and returns response.
   *
   * @param client  the client of which request will be handled
   * @param request the request
   * @param instant the time of which client was requested access
   * @param params
   * @return the response
   */
  Response execute(Client client, Identity identity, Set<String> scopes, Request request, DateTime instant, Map<String, String> params);
}
