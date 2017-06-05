package com.clouway.oauth2;

import com.clouway.friendlyserve.RsJson;
import com.clouway.friendlyserve.RsWrap;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Set;

/**
 * BearerTokenResponse is representing the response which is returned by the OAuth Server when token is generated.
 * <p/>
 * Bearer Token Usage is described in <a href="https://tools.ietf.org/html/rfc6750">RFC6750</a>
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class BearerTokenResponse extends RsWrap {

  public BearerTokenResponse(String accessToken, Long expiresInSeconds, Set<String> scopes, String refreshToken, String encodedIdToken) {
    super(new RsJson(createToken(accessToken, expiresInSeconds, scopes, refreshToken, encodedIdToken)));
  }

  public BearerTokenResponse(String accessToken, Long expiresInSeconds, Set<String> scopes, String refreshToken) {
    this(accessToken, expiresInSeconds, scopes, refreshToken, "");
  }

  private static JsonElement createToken(String accessToken, Long expiresInSeconds, Set<String> scopes, String refreshToken, String encodedIdToken) {
    JsonObject o = new JsonObject();
    o.addProperty("access_token", accessToken);
    o.addProperty("token_type", "Bearer");
    o.addProperty("expires_in", expiresInSeconds);

    if (!scopes.isEmpty()) {
      o.addProperty("scope", Joiner.on(" ").join(scopes));
    }

    o.addProperty("refresh_token", refreshToken);

    if (!Strings.isNullOrEmpty(encodedIdToken)) {
      o.addProperty("id_token", encodedIdToken);
    }

    return o;
  }
}
