package com.example.auth.core.client;

import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClientAuthenticationImplTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();
  @Mock
  ClientRepository repository;

  private ClientAuthentication clientAuthentication;

  @Before
  public void setUp() throws Exception {
    clientAuthentication = new ClientAuthenticationImpl(repository);
  }

  @Test
  public void authentication() throws Exception {
    final Client client = new Client("id123", "secret123", "name1", "url1", "description1", "redirectURI1");
    context.checking(new Expectations() {{
      oneOf(repository).findById("id123");
      will(returnValue(Optional.of(client)));
    }});

    assertTrue(clientAuthentication.authenticate("id123", "secret123"));
  }

  @Test
  public void authWithWrongId() throws Exception {
    context.checking(new Expectations() {{
      oneOf(repository).findById("id321");
      will(returnValue(Optional.absent()));
    }});
    assertFalse(clientAuthentication.authenticate("id321", "secret123"));
  }

  @Test
  public void authWithWrongSecret() throws Exception {
    final Client client = new Client("id321", "secret3", "name1", "url1", "description1", "redirectURI1");
    context.checking(new Expectations() {{
      oneOf(repository).findById("id321");
      will(returnValue(Optional.of(client)));
    }});

    assertFalse(clientAuthentication.authenticate("id321", "secret123"));
  }

}