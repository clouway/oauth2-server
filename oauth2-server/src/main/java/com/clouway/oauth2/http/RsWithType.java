package com.clouway.oauth2.http;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsWithType extends RsWrap {
  /**
   * Type header.
   */
  private static final String HEADER = "Content-Type";

  public RsWithType(Response res, String type) {
    super(new RsWithHeader(res, HEADER, type));
  }
}
