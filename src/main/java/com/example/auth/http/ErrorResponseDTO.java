package com.example.auth.http;

import com.google.gson.annotations.SerializedName;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class ErrorResponseDTO {
  @SerializedName("error")
  private final String code;

  @SerializedName("error_description")
  private final String description;

  public ErrorResponseDTO(String code, String description) {
    this.code = code;
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ErrorResponseDTO that = (ErrorResponseDTO) o;

    if (code != null ? !code.equals(that.code) : that.code != null) return false;
    if (description != null ? !description.equals(that.description) : that.description != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = code != null ? code.hashCode() : 0;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    return result;
  }
}