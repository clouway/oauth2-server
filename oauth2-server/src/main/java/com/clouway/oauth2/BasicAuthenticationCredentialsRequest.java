package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsBadRequest;

import static com.google.common.io.BaseEncoding.base64;

/**
 * BasicAuthenticationCredentialsRequest is using basic authentication scheme to decode {@link ClientCredentials} from
 * the Basic Authorization header.
 *
 *
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-2.3">OAuth2 Client Authentication</a>
 * @see <a href="https://tools.ietf.org/html/rfc2617#section-2">Basic Authentication Scheme</a>
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class BasicAuthenticationCredentialsRequest implements InstantaneousRequest {

  private final ClientRequest clientRequest;

  BasicAuthenticationCredentialsRequest(ClientRequest clientRequest) {
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
      throw new IllegalArgumentException("Credentials are not separated with ':'");
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
