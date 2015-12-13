package com.clouway.oauth2.http;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class HttpException extends RuntimeException {

  private final int code;

  public HttpException(int code) {
    this.code = code;
  }

  public int code() {
    return code;
  }
}
