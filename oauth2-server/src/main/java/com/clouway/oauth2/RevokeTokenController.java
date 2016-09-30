package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsEmpty;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class RevokeTokenController implements ClientRequest {

  private final ClientRepository clientRepository;
  private final Tokens tokens;

  RevokeTokenController(ClientRepository clientRepository, Tokens tokens) {
    this.clientRepository = clientRepository;
    this.tokens = tokens;
  }

  @Override
  public Response handleAsOf(Request request, ClientCredentials credentials, DateTime instant) {
    String token = request.param("token");

    Optional<Token> possibleToken = tokens.getNotExpiredToken(token, instant);
    if (!possibleToken.isPresent()) {
      return OAuthError.invalidRequest();
    }

    Optional<Client> possibleClient = clientRepository.findById(possibleToken.get().clientId);
    if (!possibleClient.isPresent()) {
      return OAuthError.invalidClient();
    }

    Client client = possibleClient.get();

    if (!client.credentialsMatch(credentials)) {
      return OAuthError.invalidClient();
    }

    tokens.revokeToken(token);

    return new RsEmpty();
  }

}
