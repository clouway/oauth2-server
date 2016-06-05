package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.google.common.base.Optional;

import java.util.Date;

import static com.google.common.io.BaseEncoding.base64;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class ClientController implements InstantaneousRequest {

  private final ClientRepository clientRepository;
  private final ClientActivity clientActivity;

  ClientController(ClientRepository clientRepository, ClientActivity clientActivity) {
    this.clientRepository = clientRepository;
    this.clientActivity = clientActivity;
  }

  @Override
  public Response handleAsOf(Request request, Date instant) {
    String[] credentials = decodeCredentials(request).split(":");

    String clientId = credentials[0];
    String clientSecret = credentials[1];

    Optional<Client> opt = clientRepository.findById(clientId);

    // Client was not authorized
    if (!opt.isPresent()) {
      return OAuthError.unathorizedClient();
    }

    Client client = opt.get();

    // Client secret did not match?
    if (!client.secret.equalsIgnoreCase(clientSecret)) {
      return OAuthError.unathorizedClient();
    }

    return clientActivity.execute(client, request, instant);
  }

  private String decodeCredentials(Request request) {
    String authHeader = request.header("Authorization");

    String credentials = authHeader.substring(6);

    return new String(base64().decode(credentials));
  }
}
