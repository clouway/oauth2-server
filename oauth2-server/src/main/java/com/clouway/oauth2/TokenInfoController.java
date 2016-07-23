package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsJson;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.Tokens;
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

    Optional<Token> possibleToken = tokens.getNotExpiredToken(accessToken, instantTime);
    if (!possibleToken.isPresent()) {
      return OAuthError.invalidRequest();
    }

    Token token = possibleToken.get();

    JsonObject o = new JsonObject();
    o.addProperty("sub", token.identityId);
    o.addProperty("exp", token.expirationTimestamp());
    o.addProperty("expires_in", token.expiresInSeconds);

    return new RsJson(o);
  }
}
