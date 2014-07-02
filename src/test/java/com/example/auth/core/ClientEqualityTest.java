package com.example.auth.core;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class ClientEqualityTest {
  @Test
  public void areEqual() {
    Client client1 = new Client("id", "secret", "name", "url", "description", "redirectURI");
    Client client2 = new Client("id", "secret", "name", "url", "description", "redirectURI");

    assertThat(client1, is(client2));
  }

  @Test
  public void areNotEqual() {
    Client client1 = new Client("id1", "secret1", "name1", "url1", "description1", "redirectURI1");
    Client client2 = new Client("id2", "secret2", "name2", "url2", "description2", "redirectURI2");

    assertThat(client1, is(not(client2)));
  }
}