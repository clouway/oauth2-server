package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsRedirect;
import com.clouway.oauth2.http.Take;
import com.clouway.oauth2.user.UserIdFinder;
import com.google.common.base.Optional;

import java.io.IOException;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class IdentityController implements Take {

  private final ClientRepository clientRepository;
  private final UserIdFinder userIdFinder;
  private final IdentityActivity identityActivity;

  public IdentityController(ClientRepository clientRepository, UserIdFinder userIdFinder, IdentityActivity identityActivity) {
    this.clientRepository = clientRepository;
    this.userIdFinder = userIdFinder;
    this.identityActivity = identityActivity;
  }

  @Override
  public Response ack(Request request) throws IOException {
    String clientId = request.param("client_id");

    Optional<String> optUser = userIdFinder.find(request);

    if (!optUser.isPresent()) {
      return new RsRedirect("/login");
    }

    Optional<Client> opt = clientRepository.findById(clientId);

    if (!opt.isPresent()) {
      return OAuthError.unathorizedClient();
    }

    Client client = opt.get();

    return identityActivity.execute(client, optUser.get(), request);
  }
}
