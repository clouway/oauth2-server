package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;

/**
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface ClientRequest {

  Response handleAsOf(Request request, ClientCredentials credentials, DateTime instant);

}
