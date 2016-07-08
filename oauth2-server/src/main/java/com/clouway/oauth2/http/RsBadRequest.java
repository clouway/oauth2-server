package com.clouway.oauth2.http;

import java.net.HttpURLConnection;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsBadRequest extends RsWrap {

  public RsBadRequest() {
    this(new RsEmpty());
  }

  public RsBadRequest(Response response) {
    super(new RsWithStatus(HttpURLConnection.HTTP_BAD_REQUEST, response));
  }
}
