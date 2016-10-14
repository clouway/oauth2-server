package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRepository;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public abstract class ClientRepositoryContractTest {

  private ClientRepository repository;

  @Before
  public void setUp() throws Exception {
    repository = createRepository();

  }

  @Test
  public void findById() throws Exception {
    Client client = new Client("id1", "secret1", "description1", Collections.singleton("redirectURI1"));
    repository.register(client);

    Optional<Client> actualClient = repository.findById("id1");

    assertThat(actualClient.get(), is(client));
  }

  @Test
  public void notExistingId() throws Exception {
    Optional<Client> client = repository.findById("id2");

    assertFalse(client.isPresent());
  }

 public abstract ClientRepository createRepository();

}
