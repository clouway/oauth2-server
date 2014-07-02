package com.example.auth.http;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class TokenRequestDTO {
  private String grantType;
  private String username;
  private String password;

  public TokenRequestDTO(String grantType, String username, String password) {
    this.grantType = grantType;
    this.username = username;
    this.password = password;
  }

  public String getGrantType() {
    return grantType;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}