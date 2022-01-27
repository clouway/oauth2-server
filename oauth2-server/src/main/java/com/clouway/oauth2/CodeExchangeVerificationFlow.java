package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.authorization.Authorization;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.codechallenge.CodeVerifier;
import com.clouway.oauth2.common.DateTime;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class CodeExchangeVerificationFlow implements AuthorizedClientActivity {
  private final CodeVerifier codeVerifier;
  private final AuthorizedClientActivity authorizedClientActivity;

  CodeExchangeVerificationFlow(CodeVerifier codeVerifier, AuthorizedClientActivity authorizedClientActivity) {
    this.codeVerifier = codeVerifier;
    this.authorizedClientActivity = authorizedClientActivity;
  }

  @Override
  public Response execute(Authorization authorization, Client client, Request request, DateTime instant) {
    String codeVerifierValue = request.param("code_verifier") == null ? "" : request.param("code_verifier");

    if (!codeVerifier.verify(authorization.codeChallenge, codeVerifierValue)) {
      return OAuthError.invalidGrant("code verification failed");
    }
    return authorizedClientActivity.execute(authorization, client, request, instant);
  }
}
