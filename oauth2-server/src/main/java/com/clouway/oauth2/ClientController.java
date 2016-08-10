package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.google.common.base.Optional;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class ClientController implements ClientRequest {

  private final ClientRepository clientRepository;
  private final ClientActivity clientActivity;

  ClientController(ClientRepository clientRepository, ClientActivity clientActivity) {
    this.clientRepository = clientRepository;
    this.clientActivity = clientActivity;
  }

  @Override
  public Response handleAsOf(Request request, ClientCredentials credentials, DateTime instant) {
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


}
