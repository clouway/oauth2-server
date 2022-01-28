package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.RsRedirect;
import com.clouway.oauth2.common.DateTime;
import com.google.common.base.Optional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * IdentityController is responsible for determining the Identity of the caller. Sent request to this controller
 * should contain information about the client application and for Granting User (user session is determined from {@link Request}).
 * <p/>
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
final class IdentityController implements InstantaneousRequest {

  private final ResourceOwnerIdentityFinder identityFinder;
  private final IdentityActivity identityActivity;
  private final String loginPageUrl;

  IdentityController(ResourceOwnerIdentityFinder identityFinder, IdentityActivity identityActivity, String loginPageUrl) {
    this.identityFinder = identityFinder;
    this.identityActivity = identityActivity;
    this.loginPageUrl = loginPageUrl;
  }

  @Override
  public Response handleAsOf(Request request, DateTime instantTime) {
    Optional<String> optIdentity = identityFinder.find(request, instantTime);
    // Browser should be redirected to login page when Identity is not found
    if (!optIdentity.isPresent()) {
      String continueTo = queryParams(request);
      return new RsRedirect(loginPageUrl + continueTo);
    }

    return identityActivity.execute(optIdentity.get(), request, instantTime);
  }

  private String queryParams(Request request) {
    String params = "";
    for (String param : request.names()) {
      params += "&" + param + "=" + request.param(param);
    }
    try {
      return URLEncoder.encode(request.path() + "?" + params.substring(1, params.length()), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }
}
