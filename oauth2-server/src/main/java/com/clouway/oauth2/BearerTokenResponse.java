package com.clouway.oauth2;

import com.clouway.friendlyserve.RsJson;
import com.clouway.friendlyserve.RsWrap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * BearerTokenResponse is representing the response which is returned by the OAuth Server when token is generated.
 * <p/>
 * Bearer Token Usage is described in <a href="https://tools.ietf.org/html/rfc6750">RFC6750</a>
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class BearerTokenResponse extends RsWrap {

  public BearerTokenResponse(String accessToken, Long expiresInSeconds, String refreshToken) {
    super(new RsJson(createToken(accessToken, expiresInSeconds, refreshToken)
    ));
  }

  private static JsonElement createToken(String accessToken, Long expiresInSeconds, String refreshToken) {
    JsonObject o = new JsonObject();
    o.addProperty("access_token", accessToken);
    o.addProperty("token_type", "Bearer");
    o.addProperty("expires_in", expiresInSeconds);
    o.addProperty("refresh_token", refreshToken);
    return o;
  }
}
