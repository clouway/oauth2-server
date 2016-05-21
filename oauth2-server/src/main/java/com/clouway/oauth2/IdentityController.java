package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsRedirect;
import com.clouway.oauth2.http.Take;
import com.clouway.oauth2.user.IdentityFinder;
import com.google.common.base.Optional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * IdentityController is responsible for determining the Identity of the caller. Sent request to this controller
 * should contain information about the client application and for Granting User (user session is determined from {@link Request}).
 * <p/>
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class IdentityController implements Take {

  private final ClientRepository clientRepository;
  private final IdentityFinder identityFinder;
  private final IdentityActivity identityActivity;

  public IdentityController(ClientRepository clientRepository, IdentityFinder identityFinder, IdentityActivity identityActivity) {
    this.clientRepository = clientRepository;
    this.identityFinder = identityFinder;
    this.identityActivity = identityActivity;
  }

  @Override
  public Response ack(Request request) throws IOException {
    String clientId = request.param("client_id");

    Optional<Client> opt = clientRepository.findById(clientId);

    if (!opt.isPresent()) {
      return OAuthError.unathorizedClient();
    }

    Client client = opt.get();
    Optional<String> optUser = identityFinder.find(request);

    // Users should move back to the same path after authorization passes and all requested params
    if (!optUser.isPresent()) {
      String continueTo = queryParams(request);
      // TODO (mgenov): configurable login page + param?
      return new RsRedirect("/r/oauth/login?continue=" + continueTo);
    }

    return identityActivity.execute(client, optUser.get(), request);
  }

  private String queryParams(Request request) {
    String params = "";
    for (String param : request.names()) {
      params += "&" + param + "=" + request.param(param);
    }
    try {
      return URLEncoder.encode(request.path() + "?" + params.substring(1, params.length()), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }
}
