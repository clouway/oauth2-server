package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.Take;

import java.io.IOException;
import java.util.Date;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class InstantaneousRequestController implements Take{

  private final InstantaneousRequest instantaneousRequest;

  public InstantaneousRequestController(InstantaneousRequest request) {
    this.instantaneousRequest = request;
  }

  @Override
  public Response ack(Request request) throws IOException {
    return instantaneousRequest.handleAsOf(request, new Date());
  }
}
