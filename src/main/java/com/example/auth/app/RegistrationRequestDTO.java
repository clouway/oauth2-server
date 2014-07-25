package com.example.auth.app;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class RegistrationRequestDTO {
  private String name;
  private String url;
  private String description;
  private String redirectURI;

  RegistrationRequestDTO() {
  }

  RegistrationRequestDTO(String name, String url, String description, String redirectURI) {
    this.name = name;
    this.url = url;
    this.description = description;
    this.redirectURI = redirectURI;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getDescription() {
    return description;
  }

  public String getRedirectURI() {
    return redirectURI;
  }
}