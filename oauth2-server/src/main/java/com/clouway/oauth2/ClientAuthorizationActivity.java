package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsRedirect;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.authorization.AuthorizationRequest;
import com.clouway.oauth2.authorization.ClientAuthorizationRepository;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientFinder;
import com.clouway.oauth2.codechallenge.CodeChallenge;
import com.clouway.oauth2.util.Params;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class ClientAuthorizationActivity implements IdentityActivity {
  private final ClientFinder clientFinder;
  private final ClientAuthorizationRepository clientAuthorizationRepository;

  ClientAuthorizationActivity(ClientFinder clientFinder, ClientAuthorizationRepository clientAuthorizationRepository) {
    this.clientFinder = clientFinder;
    this.clientAuthorizationRepository = clientAuthorizationRepository;
  }

  @Override
  public Response execute(String identityId, Request request) {
    String responseType = request.param("response_type");
    String clientId = request.param("client_id");
    String requestedUrl = request.param("redirect_uri");
    String state = request.param("state");
    String scope = request.param("scope") == null ? "" : request.param("scope");
    String codeChallengeValue = request.param("code_challenge") == null ? "" : request.param("code_challenge");
    String codeVerifierMethod = request.param("code_challenge_method") == null ? "" : request.param("code_challenge_method");
    CodeChallenge codeChallenge = new CodeChallenge(codeChallengeValue, codeVerifierMethod);

    Map<String, String> params = new Params().parse(request, "response_type", "redirect_uri", "state", "scope", "code_challenge", "code_challenge_method");

    Optional<Client> possibleClientResponse = clientFinder.findClient(clientId);

    if (!possibleClientResponse.isPresent()) {
      return OAuthError.unauthorizedClient(String.format("Unknown client '%s'", clientId));
    }

    Client client = possibleClientResponse.get();

    Optional<String> possibleRedirectUrl = client.determineRedirectUrl(requestedUrl);

    if (!possibleRedirectUrl.isPresent()) {
      return OAuthError.unauthorizedClient("Client Redirect URL is not matching the configured one.");
    }

    Set<String> scopes = Sets.newTreeSet(Splitter.on(" ").omitEmptyStrings().split(scope));

    Optional<Authorization> possibleAuthorizationResponse = clientAuthorizationRepository.authorize(new AuthorizationRequest(client, identityId, responseType, scopes, codeChallenge, params));

    // RFC-6749 - Section: 4.2.2.1
    // The authorization server redirects the user-agent by
    // sending the following HTTP response:
    // HTTP/1.1 302 Found
    // Location: https://client.example.com/cb#error=access_denied&state=xyz
    if (!possibleAuthorizationResponse.isPresent()) {
      return new RsRedirect(possibleRedirectUrl.get() + "?error=access_denied");
    }

    String callback = possibleRedirectUrl.get();

    Authorization authorization = possibleAuthorizationResponse.get();
    return new RsRedirect(createCallbackUrl(callback, authorization.code, state));
  }

  private String createCallbackUrl(String callback, String code, String state) {
    String url;
    if (Strings.isNullOrEmpty(state)) {
      url = String.format("%s?code=%s", callback, code);
    } else {
      url = String.format("%s?code=%s&state=%s", callback, code, state);
    }

    return url;
  }
}
