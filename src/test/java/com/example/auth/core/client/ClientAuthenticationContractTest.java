package com.example.auth.core.client;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public abstract class ClientAuthenticationContractTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientAuthentication clientAuthentication;

  @Before
  public void setUp() throws Exception {
    clientAuthentication = createClientAuthentication();

  }

  protected abstract ClientAuthentication createClientAuthentication();

  @Test
  public void authentication() throws Exception {
    Client client = new Client("id123", "secret123", "name1", "url1", "description1", "redirectURI1");
    save(client);

    assertTrue(clientAuthentication.authenticate("id123", "secret123"));
  }

  @Test
  public void authWithWrongId() throws Exception {
    assertFalse(clientAuthentication.authenticate("id321", "secret123"));
  }

  @Test
  public void authWithWrongSecret() throws Exception {
    Client client = new Client("id123", "secret123", "name1", "url1", "description1", "redirectURI1");
    save(client);

    assertFalse(clientAuthentication.authenticate("id321", "secret123"));
  }

  protected abstract void save(Client client);
}
