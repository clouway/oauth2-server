package com.clouway.oauth2.exampleapp;

import com.google.gson.annotations.SerializedName;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class RegistrationResponseDTO {
  @SerializedName("client_id")
  private final String clientId;

  @SerializedName("client_secret")
  private final String clientSecret;

  RegistrationResponseDTO(String clientId, String clientSecret) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RegistrationResponseDTO that = (RegistrationResponseDTO) o;

    if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
    if (clientSecret != null ? !clientSecret.equals(that.clientSecret) : that.clientSecret != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = clientId != null ? clientId.hashCode() : 0;
    result = 31 * result + (clientSecret != null ? clientSecret.hashCode() : 0);
    return result;
  }
}