package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.RsRedirect;
import com.clouway.oauth2.http.Take;
import com.clouway.oauth2.user.IdentityFinder;
import com.google.common.base.Optional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * IdentityController is responsible for determining the Identity of the caller. Sent request to this controller
 * should contain information about the client application and for Granting User (user session is determined from {@link Request}).
 * <p/>
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
final class IdentityController implements Take {

  private final IdentityFinder identityFinder;
  private final IdentityActivity identityActivity;

  IdentityController(IdentityFinder identityFinder, IdentityActivity identityActivity) {
    this.identityFinder = identityFinder;
    this.identityActivity = identityActivity;
  }

  @Override
  public Response ack(Request request) throws IOException {

    Optional<String> optIdentity = identityFinder.find(request);
    // Browser should be redirected to login page when Identity is not found
    if (!optIdentity.isPresent()) {
      String continueTo = queryParams(request);
      // TODO (mgenov): configurable login page + param?
      return new RsRedirect("/r/oauth/login?continue=" + continueTo);
    }

    return identityActivity.execute(optIdentity.get(), request);
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
