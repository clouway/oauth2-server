package com.clouway.oauth2.http;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsWithStatus extends RsWrap {
  public RsWithStatus(final int statusCode, final Response source) {
    super(new RsWrap(source) {
      @Override
      public Status status() {
        return new Status(statusCode);
      }
    });
  }
}
