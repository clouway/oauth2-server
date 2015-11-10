package com.example.auth.memory;

import com.clouway.oauth2.ResourceOwner;
import com.clouway.oauth2.Session;
import com.clouway.oauth2.token.TokenGenerator;
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
public class InMemoryResourceOwnerRepositoryTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private TokenGenerator tokenGenerator = context.mock(TokenGenerator.class);

  private InMemoryResourceOwnerRepository repository = new InMemoryResourceOwnerRepository(tokenGenerator);
  private String remoteAddress= "123.1.1.1";

  @Test
  public void correctCredentials() throws Exception {
    ResourceOwner owner = new ResourceOwner("username", "password");

    context.checking(new Expectations() {{
      oneOf(tokenGenerator).generate();
      will(returnValue("token1234"));
    }});

    repository.save(owner);

    Optional<Session> session = repository.auth(owner.username, owner.password, remoteAddress);

    assertThat(session.get().value, is("token1234"));
  }

  @Test
  public void notExistingUser() throws Exception {
    Optional<Session> session = repository.auth("joro", "123456", remoteAddress);

    assertFalse(session.isPresent());
  }

  @Test
  public void wrongPassword() throws Exception {
    ResourceOwner owner = new ResourceOwner("username", "pass");

    repository.save(owner);

    Optional<Session> session = repository.auth("username", "123456", remoteAddress);

    assertFalse(session.isPresent());
  }

  @Test
  public void existingSession() throws Exception {
    final ResourceOwner owner = new ResourceOwner("Ivan", "password");
    final Session session = new Session("token4321");

    context.checking(new Expectations() {{
      oneOf(tokenGenerator).generate();
      will(returnValue(session.value));
    }});

    repository.save(owner);
    repository.auth(owner.username, owner.password, remoteAddress);

    Boolean sessionIsPresented = repository.exists(session);

    assertTrue(sessionIsPresented);
  }

  @Test
  public void notExistingSession() throws Exception {
    Session session = new Session("session123");

    Boolean sessionIsPresented = repository.exists(session);

    assertFalse(sessionIsPresented);
  }
}