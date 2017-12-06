package com.clouway.oauth2.codechallenge;

import com.google.common.base.Objects;

/**
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 */
public class CodeChallenge {
  public String transformedCodeChallenge = "";
  public String method = "";

  public CodeChallenge(String transformedCodeChallenge, String method) {
    this.transformedCodeChallenge = transformedCodeChallenge;
    this.method = method;
  }

  public boolean isProvided() {
    return !this.transformedCodeChallenge.isEmpty() && !this.method.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CodeChallenge that = (CodeChallenge) o;
    return Objects.equal(transformedCodeChallenge, that.transformedCodeChallenge) &&
            Objects.equal(method, that.method);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(transformedCodeChallenge, method);
  }
}
