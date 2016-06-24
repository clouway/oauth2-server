package com.clouway.oauth2.http;

import com.google.common.base.Optional;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class TkFork implements Take {

  private transient final Fork[] forks;

  public TkFork(Fork... forks) {
    this.forks = forks;
  }

  @Override
  public Response ack(Request request) throws IOException {
    for (Fork each : forks) {
      Optional<Response> possibleResponse = each.route(request);
      if (possibleResponse.isPresent()) {
        return possibleResponse.get();
      }
    }

    throw new HttpException(HttpURLConnection.HTTP_NOT_FOUND);
  }
}
