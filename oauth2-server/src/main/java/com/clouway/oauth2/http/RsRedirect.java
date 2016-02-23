package com.clouway.oauth2.http;

import javax.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsRedirect extends RsWrap {

  private final String redirectUrl;

  public RsRedirect(String redirectUrl) {
    super(new RsEmpty());
    this.redirectUrl = redirectUrl;
  }

  @Override
  public Status status() {
    return new Status(HttpURLConnection.HTTP_MOVED_TEMP, redirectUrl);
  }

}
