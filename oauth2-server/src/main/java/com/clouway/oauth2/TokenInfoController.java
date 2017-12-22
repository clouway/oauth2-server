package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsJson;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.IdTokenFactory;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.IdentityFinder;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class TokenInfoController implements InstantaneousRequest {
  private final Tokens tokens;
  private final IdentityFinder identityFinder;
  private final IdTokenFactory idTokenFactory;

  TokenInfoController(Tokens tokens, IdentityFinder identityFinder, IdTokenFactory idTokenFactory) {
    this.tokens = tokens;
    this.identityFinder = identityFinder;
    this.idTokenFactory = idTokenFactory;
  }

  @Override
  public Response handleAsOf(Request request, DateTime instantTime) {
    String accessToken = request.param("access_token");

    Optional<BearerToken> possibleToken = tokens.findTokenAvailableAt(accessToken, instantTime);
    if (!possibleToken.isPresent()) {
      return OAuthError.invalidRequest("Access token not found");
    }

    BearerToken token = possibleToken.get();
    Map<String, String> params = token.params != null ? token.params : Maps.<String, String>newHashMap();
    Optional<Identity> possibleIdentity = identityFinder.findIdentity(token.identityId, token.grantType, instantTime, params);
    Identity identity = possibleIdentity.get();
    String host = request.header("Host");
    Optional<String> possibleIdToken = idTokenFactory.create(host, token.clientId, identity,
            token.ttlSeconds(instantTime), instantTime);

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

    if (possibleIdToken.isPresent()) {
      o.addProperty("id_token", possibleIdToken.get());
    }

    return new RsJson(o);
  }
}
