package com.example.auth.app;

import com.google.gson.annotations.SerializedName;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class TokenDTO {

  @SerializedName("access_token")
  private final String value;

  @SerializedName("refresh_token")
  private final String refreshValue;

  @SerializedName("token_type")
  private final String type;

  @SerializedName("expires_in")
  private final Long expiresIn;

  TokenDTO(String value, String refreshValue, String type, Long expiresIn) {
    this.value = value;
    this.refreshValue = refreshValue;
    this.type = type;
    this.expiresIn = expiresIn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TokenDTO tokenDTO = (TokenDTO) o;

    if (type != null ? !type.equals(tokenDTO.type) : tokenDTO.type != null) return false;
    if (value != null ? !value.equals(tokenDTO.value) : tokenDTO.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (type != null ? type.hashCode() : 0);
    return result;
  }
}