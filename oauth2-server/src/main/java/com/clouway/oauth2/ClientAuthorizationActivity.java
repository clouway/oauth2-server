package com.clouway.oauth2;

import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsRedirect;
import com.google.common.base.Optional;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class ClientAuthorizationActivity implements IdentityActivity {
  private final ClientRepository clientRepository;
  private final ClientAuthorizationRepository clientAuthorizationRepository;

  ClientAuthorizationActivity(ClientRepository clientRepository, ClientAuthorizationRepository clientAuthorizationRepository) {
    this.clientRepository = clientRepository;
    this.clientAuthorizationRepository = clientAuthorizationRepository;
  }

  @Override
  public Response execute(String userId, Request request) {
    String responseType = request.param("response_type");
    String clientId = request.param("client_id");

    Optional<Client> possibleClientResponse = clientRepository.findById(clientId);

    if (!possibleClientResponse.isPresent()) {
      return OAuthError.unauthorizedClient();
    }

    Client client = possibleClientResponse.get();

    Optional<Authorization> possibleAuthorizationResponse = clientAuthorizationRepository.authorize(client, userId, responseType);

    // RFC-6749 - Section: 4.2.2.1
    // The authorization server redirects the user-agent by
    // sending the following HTTP response:
    // HTTP/1.1 302 Found
    // Location: https://client.example.com/cb#error=access_denied&state=xyz
    if (!possibleAuthorizationResponse.isPresent()) {
      return new RsRedirect(client.redirectURI + "?error=access_denied");
    }

    Authorization authorization = possibleAuthorizationResponse.get();
    return new RsRedirect(String.format("%s?code=%s", client.redirectURI, authorization.code));
  }
}
