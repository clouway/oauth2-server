package com.clouway.oauth2.token;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class ProvidedRefreshToken {
  public final String value;
  public final String clientId;
  public final String clientSecret;

  public ProvidedRefreshToken(String value, String clientId, String clientSecret) {

    this.value = value;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProvidedRefreshToken)) return false;

    ProvidedRefreshToken that = (ProvidedRefreshToken) o;

    if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
    if (clientSecret != null ? !clientSecret.equals(that.clientSecret) : that.clientSecret != null) return false;
    if (value != null ? !value.equals(that.value) : that.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
    result = 31 * result + (clientSecret != null ? clientSecret.hashCode() : 0);
    return result;
  }
}
