package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;

/**
 * ClientActivity is representing Activity for a single client (in the context of OAuth2).
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface ClientActivity {

  /**
   * Handles client request and returns response.
   *
   * @param client  the client of which request will be handled
   * @param request the request
   * @param instant the time of which client was requested access
   * @return the response
   */
  Response execute(Client client, Request request, DateTime instant);
}
