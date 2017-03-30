package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientRegistrationRequest;
import com.clouway.oauth2.client.ClientRepository;
import com.clouway.oauth2.exampleapp.storage.InMemoryClientRepositoryTest;
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
public class ClientRepositoryContractTest {

  private ClientRepository repository;

  @Before
  public void setUp() throws Exception {
    repository = new InMemoryClientRepositoryTest();
  }

  @Test
  public void findById() throws Exception {
    ClientRegistrationRequest request = new ClientRegistrationRequest("secret1", "description1", Collections.singleton("redirectURI1"));
    Client client = repository.register(request);

    Optional<Client> actualClient = repository.findById(client.id);

    assertThat(actualClient.get(), is(client));
  }

  @Test
  public void notExistingId() throws Exception {
    Optional<Client> client = repository.findById("id2");

    assertFalse(client.isPresent());
  }
}
