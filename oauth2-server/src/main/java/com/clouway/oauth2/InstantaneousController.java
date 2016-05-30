package com.clouway.oauth2;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.http.Response;
import com.clouway.oauth2.http.Take;

import java.io.IOException;
import java.util.Date;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public abstract class InstantaneousController implements Take {

  @Override
  public Response ack(Request request) throws IOException {
    return handleAsOf(request, new Date());
  }

  protected abstract Response handleAsOf(Request request, Date instant);
}
