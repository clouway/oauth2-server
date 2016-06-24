package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;

import static com.google.common.io.BaseEncoding.base64;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class AuthorizationHeaderCredentialsRequest implements InstantaneousRequest {

  private final ClientRequest clientRequest;

  public AuthorizationHeaderCredentialsRequest(ClientRequest clientRequest) {
    this.clientRequest = clientRequest;
  }

  @Override
  public Response handleAsOf(Request request, DateTime instantTime) {
    ClientCredentials clientCredentials = decodeCredentials(request);
    return clientRequest.handleAsOf(request, clientCredentials, instantTime);
  }

  private ClientCredentials decodeCredentials(Request request) {
    String[] credentials = decodeAuthorizationHeader(request).split(":");

    String clientId = credentials[0];
    String clientSecret = credentials[1];

    return new ClientCredentials(clientId, clientSecret);
  }

  private String decodeAuthorizationHeader(Request request) {
    String authHeader = request.header("Authorization");

    String credentials = authHeader.substring(6);

    return new String(base64().decode(credentials));
  }
}
