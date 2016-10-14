package com.clouway.oauth2.client;

import com.clouway.oauth2.ClientCredentials;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class Client {
  public final String id;
  public final String secret;
  public final String description;
  public final Set<String> redirectURLs;

  public Client(String id, String secret, String description, Set<String> redirectURLs) {
    this.id = id;
    this.secret = secret;
    this.description = description;
    this.redirectURLs = redirectURLs;
  }

  public Optional<String> determineRedirectUrl(String requestedUrl) {
    if (isNullOrEmpty(requestedUrl)) {
      return Optional.fromNullable(Iterables.getFirst(redirectURLs, "http://client.was.not.configured.properly.com"));
    }

    if (!redirectURLs.contains(requestedUrl)) {
      return Optional.absent();
    }

    return Optional.of(requestedUrl);
  }

  public boolean credentialsMatch(ClientCredentials credentials) {
    return secret.equalsIgnoreCase(credentials.clientSecret());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Client client = (Client) o;
    return Objects.equals(id, client.id) &&
            Objects.equals(secret, client.secret) &&
            Objects.equals(description, client.description) &&
            Objects.equals(redirectURLs, client.redirectURLs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, secret, description, redirectURLs);
  }

}