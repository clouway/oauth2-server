package com.clouway.oauth2;

import com.clouway.oauth2.client.Client;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class ClientEqualityTest {
  @Test
  public void areEqual() {
    Client client1 = new Client("id", "secret", "description", Collections.singleton("redirectURI"), true);
    Client client2 = new Client("id", "secret", "description", Collections.singleton("redirectURI"), true);

    assertThat(client1, is(client2));
  }

  @Test
  public void areNotEqual() {
    Client client1 = new Client("id1", "secret1", "description1", Collections.singleton("redirectURI1"), false);
    Client client2 = new Client("id2", "secret2", "description2", Collections.singleton("redirectURI2"), false);

    assertThat(client1, is(not(client2)));
  }
}