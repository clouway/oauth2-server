package com.example.auth.http;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class RegistrationResponseDTO {
  private final String id;
  private final String secret;

  RegistrationResponseDTO(String id, String secret) {
    this.id = id;
    this.secret = secret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RegistrationResponseDTO that = (RegistrationResponseDTO) o;

    if (id != null ? !id.equals(that.id) : that.id != null) return false;
    if (secret != null ? !secret.equals(that.secret) : that.secret != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (secret != null ? secret.hashCode() : 0);
    return result;
  }
}