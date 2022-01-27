package com.clouway.oauth2;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.friendlyserve.Take;
import com.clouway.oauth2.common.DateTime;

import java.io.IOException;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class InstantaneousRequestController implements Take {

  private final InstantaneousRequest instantaneousRequest;

  InstantaneousRequestController(InstantaneousRequest request) {
    this.instantaneousRequest = request;
  }

  @Override
  public Response ack(Request request) throws IOException {
    return instantaneousRequest.handleAsOf(request, new DateTime());
  }
}
