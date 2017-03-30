package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.google.common.base.Optional;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class ClientController implements ClientRequest {

  private final ClientFinder clientFinder;
  private final ClientActivity clientActivity;

  ClientController(ClientFinder clientFinder, ClientActivity clientActivity) {
    this.clientFinder = clientFinder;
    this.clientActivity = clientActivity;
  }

  @Override
  public Response handleAsOf(Request request, ClientCredentials credentials, DateTime instant) {
    Optional<Client> possibleResponse = clientFinder.findClient(credentials.clientId());

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
