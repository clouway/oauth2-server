package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsJson;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.gson.JsonObject;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class TokenInfoController implements InstantaneousRequest {
  private final Tokens tokens;

  TokenInfoController(Tokens tokens) {
    this.tokens = tokens;
  }

  @Override
  public Response handleAsOf(Request request, DateTime instantTime) {
    String accessToken = request.param("access_token");

    Optional<BearerToken> possibleToken = tokens.findTokenAvailableAt(accessToken, instantTime);
    if (!possibleToken.isPresent()) {
      return OAuthError.invalidRequest();
    }

    BearerToken token = possibleToken.get();

    JsonObject o = new JsonObject();
    o.addProperty("azp", token.clientId);
    o.addProperty("aud", token.clientId);
    o.addProperty("sub", token.identityId);
    o.addProperty("exp", token.expirationTimestamp());
    o.addProperty("expires_in", token.ttlSeconds(instantTime));
    o.addProperty("email", token.email);

    if (!token.scopes.isEmpty()) {
      o.addProperty("scope", Joiner.on(" ").join(token.scopes));
    }

    return new RsJson(o);
  }
}
