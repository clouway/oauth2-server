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

  public static OAuthError invalidRequest() {
    return new OAuthError("invalid_request", HttpURLConnection.HTTP_BAD_REQUEST);
  }

  public static OAuthError unauthorizedClient() {
    return new OAuthError("unauthorized_client", HttpURLConnection.HTTP_BAD_REQUEST);
  }

  public static OAuthError invalidGrant() {
    return new OAuthError("invalid_grant", HttpURLConnection.HTTP_BAD_REQUEST);
  }

  public static OAuthError unknownClient() {
    return new OAuthError(HttpURLConnection.HTTP_BAD_REQUEST, "invalid_client", "Unknown Client");
  }

  public static OAuthError invalidToken() {
    return new OAuthError(HttpURLConnection.HTTP_UNAUTHORIZED, "invalid_token", "Invalid Credentials");
  }

  private OAuthError(String errorName, int responseCode) {
    super(new RsWithStatus(responseCode, createJsonResponse(errorName)));
  }

  private OAuthError(int responseCode, String erroName, String errorDescription) {
    super(new RsWithStatus(responseCode, createJsonResponse(erroName, errorDescription)));
  }

  private static Response createJsonResponse(String errorName) {
    JsonObject o = new JsonObject();
    o.addProperty("error", errorName);
    return new RsJson(o);
  }

  private static Response createJsonResponse(String errorName, String errorDescription) {
    JsonObject o = new JsonObject();
    o.addProperty("error", errorName);
    o.addProperty("error_description", errorDescription);
    return new RsJson(o);
  }
}
