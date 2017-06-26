package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsBadRequest;
import com.google.common.base.Strings;

import static com.google.common.io.BaseEncoding.base64;

/**
 * ClientAuthenticationCredentialsRequest is using client authentication credentials request which supports
 * the following authentication schemes:
 * - basic authentication scheme to decode {@link ClientCredentials} from the Basic Authorization header.
 * - param authentication of identifying the public clients.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-2.3">OAuth2 Client Authentication</a>
 * @see <a href="https://tools.ietf.org/html/rfc2617#section-2">Basic Authentication Scheme</a>
 */
class ClientAuthenticationCredentialsRequest implements InstantaneousRequest {

  private final ClientRequest clientRequest;

  ClientAuthenticationCredentialsRequest(ClientRequest clientRequest) {
    this.clientRequest = clientRequest;
  }

  @Override
  public Response handleAsOf(Request request, DateTime instantTime) {
    String authHeader = request.header("Authorization");
    if (authHeader != null && authHeader.startsWith("Basic")) {
      String credentialsString = trimLeadingBasicText(authHeader);

      try {
        String decoded = new String(base64().decode(credentialsString));

        ClientCredentials clientCredentials = parseCredentials(decoded);
        return clientRequest.handleAsOf(request, clientCredentials, instantTime);

      } catch (IllegalArgumentException e) {
        return new RsBadRequest();
      }
    }

    String clientId = request.param("client_id");
    if (Strings.isNullOrEmpty(clientId)) {
      return new RsBadRequest();
    }

    return clientRequest.handleAsOf(request, new ClientCredentials(clientId, ""), instantTime);
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
