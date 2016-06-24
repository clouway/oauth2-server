package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;

/**
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface ClientRequest {

  Response handleAsOf(ClientCredentials credentials, Request request, DateTime instant);

}
