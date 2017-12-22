package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsEmpty;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class RevokeTokenController implements ClientRequest {

  private final ClientFinder clientFinder;
  private final Tokens tokens;

  RevokeTokenController(ClientFinder clientFinder, Tokens tokens) {
    this.clientFinder = clientFinder;
    this.tokens = tokens;
  }

  @Override
  public Response handleAsOf(Request request, ClientCredentials credentials, DateTime instant) {
    String token = request.param("token");

    Optional<BearerToken> possibleToken = tokens.findTokenAvailableAt(token, instant);
    if (!possibleToken.isPresent()) {
      return OAuthError.invalidRequest("Token was not found.");
    }

    Optional<Client> possibleClient = clientFinder.findClient(possibleToken.get().clientId);
    if (!possibleClient.isPresent()) {
      return OAuthError.unauthorizedClient("A client with id:" + possibleToken.get().clientId + " was either not found or is not authorized.");
    }

    Client client = possibleClient.get();

    if (!client.credentialsMatch(credentials)) {
      return OAuthError.unauthorizedClient("Client credentials mismatch.");
    }

    tokens.revokeToken(token);

    return new RsEmpty();
  }

}
