package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.google.common.base.Optional;

import static com.google.common.io.BaseEncoding.base64;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class ClientController implements InstantaneousRequest, ClientRequest {

  private final ClientRepository clientRepository;
  private final ClientActivity clientActivity;

  ClientController(ClientRepository clientRepository, ClientActivity clientActivity) {
    this.clientRepository = clientRepository;
    this.clientActivity = clientActivity;
  }

  @Override
  public Response handleAsOf(Request request, DateTime instant) {
    ClientCredentials credentials = decodeCredentials(request);

    return handleAsOf(credentials, request, instant);
  }

  @Override
  public Response handleAsOf(ClientCredentials credentials, Request request, DateTime instant) {
    Optional<Client> possibleResponse = clientRepository.findById(credentials.clientId());

    // Client was not authorized
    if (!possibleResponse.isPresent()) {
      return OAuthError.unauthorizedClient();
    }

    Client client = possibleResponse.get();

    // Client credentials did not match?
    if (!client.credentialsMatch(credentials)) {
      return OAuthError.unauthorizedClient();
    }

    return clientActivity.execute(client, request, instant);
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
