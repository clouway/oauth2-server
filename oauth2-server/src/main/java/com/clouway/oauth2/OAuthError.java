package com.clouway.oauth2;

import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsJson;
import com.clouway.oauth2.http.RsWithStatus;
import com.clouway.oauth2.http.RsWrap;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class OAuthError extends RsWrap {

  public static OAuthError unathorizedClient() {
    return new OAuthError("unauthorized_client");
  }

  public static OAuthError invalidGrant() {
    return new OAuthError("invalid_grant");
  }

  private OAuthError(String errorName) {
    super(new RsWithStatus(HttpURLConnection.HTTP_BAD_REQUEST, createJsonResponse(errorName)));

  }

  private static Response createJsonResponse(String errorName) {
    JsonObject o = new JsonObject();
    o.addProperty("error", errorName);
    return new RsJson(o);
  }
}
