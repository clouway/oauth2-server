package com.clouway.oauth2.http;

/**
 * Status is representing HTTP response status.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class Status {
  public final int code;
  public final String redirectUrl;

  public Status(int code) {
    this.code = code;
    this.redirectUrl = null;
  }

  public Status(int code, String redirectUrl) {
    this.code = code;
    this.redirectUrl = redirectUrl;
  }
}
