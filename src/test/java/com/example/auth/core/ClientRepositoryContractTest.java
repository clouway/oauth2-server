package com.example.auth.core;

import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public abstract class ClientRepositoryContractTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private TokenGenerator tokenGenerator = context.mock(TokenGenerator.class);

  private ClientRepository repository = create(tokenGenerator);

  @Test
  public void findById() throws Exception {
    registerClient("id1", "secret1", new RegistrationRequest("name1", "url1", "description1", "redirectURI1"));

    Optional<Client> actualClient = repository.findById("id1");
    Client expectedClient = new Client("id1", "secret1", "name1", "url1", "description1", "redirectURI1");

    assertThat(actualClient.get(), is(expectedClient));
  }

  @Test
  public void findAnotherClientById() throws Exception {
    registerClient("id2", "secret2", new RegistrationRequest("name2", "url2", "description2", "redirectURI2"));

    Optional<Client> actualClient = repository.findById("id2");
    Client expectedClient = new Client("id2", "secret2", "name2", "url2", "description2", "redirectURI2");

    assertThat(actualClient.get(), is(expectedClient));
  }

  private void registerClient(final String id, final String secret, final RegistrationRequest request) {
    context.checking(new Expectations() {{
      oneOf(tokenGenerator).generate();
      will(returnValue(id));

      oneOf(tokenGenerator).generate();
      will(returnValue(secret));
    }});

    repository.register(request);
  }

  protected abstract ClientRepository create(TokenGenerator tokenGenerator);
}