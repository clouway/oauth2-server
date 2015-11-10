package com.clouway.oauth2.client;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class RegistrationRequest {
  public final String name;
  public final String url;
  public final String description;
  public final String redirectURI;

  public RegistrationRequest(String name, String url, String description, String redirectURI) {
    this.name = name;
    this.url = url;
    this.description = description;
    this.redirectURI = redirectURI;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RegistrationRequest that = (RegistrationRequest) o;

    if (description != null ? !description.equals(that.description) : that.description != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (redirectURI != null ? !redirectURI.equals(that.redirectURI) : that.redirectURI != null) return false;
    if (url != null ? !url.equals(that.url) : that.url != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (url != null ? url.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (redirectURI != null ? redirectURI.hashCode() : 0);
    return result;
  }
}