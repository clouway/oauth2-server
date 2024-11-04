package com.clouway.oauth2.client;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * ClientCredentials is representing pair of id and secret of the client app.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class ClientCredentials {
  private final String clientId;
  private final String clientSecret;

  public ClientCredentials(String clientId, String clientSecret) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  public String clientId() {
    return clientId;
  }

  public String clientSecret() {
    return clientSecret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientCredentials that = (ClientCredentials) o;
    return Objects.equal(clientId, that.clientId) &&
            Objects.equal(clientSecret, that.clientSecret);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(clientId, clientSecret);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("clientId", clientId)
            .add("clientSecret", clientSecret)
            .toString();
  }
}
