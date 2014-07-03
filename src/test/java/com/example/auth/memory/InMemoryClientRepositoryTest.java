package com.example.auth.memory;

import com.example.auth.core.Client;
import com.example.auth.core.ClientRegister;
import com.example.auth.core.RegistrationRequest;
import com.example.auth.core.TokenGenerator;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryClientRepositoryTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private TokenGenerator tokenGenerator = context.mock(TokenGenerator.class);

  private InMemoryClientRepository repository = new InMemoryClientRepository(tokenGenerator);

  @Test
  public void findById() throws Exception {
    registerClient("id1", "secret1", new RegistrationRequest("name1", "url1", "description1", "redirectURI1"));

    Optional<Client> actualClient = repository.findById("id1");
    Optional<Client> expectedClient = Optional.of(new Client("id1", "secret1", "name1", "url1", "description1", "redirectURI1"));

    assertThat(actualClient, is(expectedClient));
  }

  @Test
  public void notExistingId() throws Exception {
    Optional<Client> client = repository.findById("id2");

    assertFalse(client.isPresent());
  }

  @Test
  public void authentication() throws Exception {
    registerClient("id123", "secret123", new RegistrationRequest("name123", "url123", "description123", "redirectURI123"));

    assertTrue(repository.authenticate("id123", "secret123"));
  }

  @Test
  public void authWithWrongId() throws Exception {
    assertFalse(repository.authenticate("id321", "secret123"));
  }

  @Test
  public void authWithWrongSecret() throws Exception {
    registerClient("id321", "secret321", new RegistrationRequest("name321", "url321", "description321", "redirectURI321"));

    assertFalse(repository.authenticate("id321", "secret123"));
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
}