package com.clouway.oauth2.http;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.net.HttpURLConnection;

/**
 * Status is representing HTTP response status.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class Status {

  public static Status badRequest() {
    return new Status(HttpURLConnection.HTTP_BAD_REQUEST);
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Status status = (Status) o;
    return code == status.code &&
            Objects.equal(redirectUrl, status.redirectUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(code, redirectUrl);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("code", code)
            .add("redirectUrl", redirectUrl)
            .toString();
  }
}
