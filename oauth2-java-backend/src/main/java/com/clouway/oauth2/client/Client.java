package com.clouway.oauth2.client;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Client {
  public final String id;
  public final String secret;
  public final String name;
  public final String url;
  public final String description;
  public final String redirectURI;

  public Client(String id, String secret, String name, String url, String description, String redirectURI) {
    this.id = id;
    this.secret = secret;
    this.name = name;
    this.url = url;
    this.description = description;
    this.redirectURI = redirectURI;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Client client = (Client) o;

    if (description != null ? !description.equals(client.description) : client.description != null) return false;
    if (id != null ? !id.equals(client.id) : client.id != null) return false;
    if (name != null ? !name.equals(client.name) : client.name != null) return false;
    if (redirectURI != null ? !redirectURI.equals(client.redirectURI) : client.redirectURI != null) return false;
    if (secret != null ? !secret.equals(client.secret) : client.secret != null) return false;
    if (url != null ? !url.equals(client.url) : client.url != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (secret != null ? secret.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (url != null ? url.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (redirectURI != null ? redirectURI.hashCode() : 0);
    return result;
  }
}