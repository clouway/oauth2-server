package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsJson;
import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.IdentityFinder;
import com.google.common.base.Optional;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class UserInfoController implements InstantaneousRequest {

  private final IdentityFinder identityFinder;
  private final Tokens tokens;

  UserInfoController(IdentityFinder identityFinder, Tokens tokens) {
    this.identityFinder = identityFinder;
    this.tokens = tokens;
  }

  @Override
  public Response handleAsOf(Request request, DateTime instantTime) {
    String accessToken = request.param("access_token");

    Optional<Token> possibleTokenResponse = tokens.findTokenAvailableAt(accessToken, instantTime);

    if (!possibleTokenResponse.isPresent()) {
      return OAuthError.invalidToken();
    }

    Token token = possibleTokenResponse.get();

    Optional<Identity> possibleIdentityResponse = identityFinder.findIdentity(token.identityId, token.grantType, instantTime);
    if (!possibleIdentityResponse.isPresent()) {
      return OAuthError.unknownClient();
    }

    Identity identity = possibleIdentityResponse.get();

    JsonObject o = new JsonObject();

    o.addProperty("id", identity.id());
    o.addProperty("name", identity.name());
    o.addProperty("email", identity.email());
    o.addProperty("given_name", identity.givenName());
    o.addProperty("family_name", identity.familyName());

    Map<String, Object> claims = identity.claims();

    for (String key : claims.keySet()) {
      Object value = claims.get(key);

      if (value instanceof String) {
        o.addProperty(key, (String) value);
      }
      if (value instanceof Number) {
        o.addProperty(key, (Number) value);
      }

      if (value instanceof Boolean) {
        o.addProperty(key, (Boolean) value);
      }
    }

    return new RsJson(o);
  }
}
