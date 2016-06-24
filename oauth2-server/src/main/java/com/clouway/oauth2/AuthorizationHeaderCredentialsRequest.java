package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsBadRequest;

import static com.google.common.io.BaseEncoding.base64;

/**
 * AuthorizationHeaderCredentialsRequest is a request which is decoding Authorization header into ClientCredentials
 * pair.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class AuthorizationHeaderCredentialsRequest implements InstantaneousRequest {

  private final ClientRequest clientRequest;

  AuthorizationHeaderCredentialsRequest(ClientRequest clientRequest) {
    this.clientRequest = clientRequest;
  }

  @Override
  public Response handleAsOf(Request request, DateTime instantTime) {
    String authHeader = request.header("Authorization");
    if (!authHeader.startsWith("Basic")) {
      return new RsBadRequest();
    }
    String credentialsString = trimLeadingBasicText(authHeader);

    try {
      String decoded = new String(base64().decode(credentialsString));

      ClientCredentials clientCredentials = parseCredentials(decoded);
      return clientRequest.handleAsOf(request, clientCredentials, instantTime);

    } catch (IllegalArgumentException e) {
      return new RsBadRequest();
    }

  }

  private ClientCredentials parseCredentials(String decodedHeader) {
    if (!decodedHeader.contains(":")) {
      throw new IllegalArgumentException("Creadentials are not separated with ':'");
    }

    String[] credentials = decodedHeader.split(":");

    String clientId = credentials[0];
    String clientSecret = credentials[1];

    return new ClientCredentials(clientId, clientSecret);
  }

  private String trimLeadingBasicText(String authHeader) {
    return authHeader.substring(6);
  }
}
