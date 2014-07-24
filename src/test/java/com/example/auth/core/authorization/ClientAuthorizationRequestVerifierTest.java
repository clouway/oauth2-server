package com.example.auth.core.authorization;

import com.example.auth.core.ClientAuthorizationRequest;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClientAuthorizationRequestVerifierTest {
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  private ClientAuthorizationRequestVerifier requestVerifier;

  private String code = "code";
  private String clientId = "clientId";
  @Mock
  private ClientAuthorizationRepository repository;

  @Before
  public void setUp() throws Exception {
    requestVerifier = new ClientAuthorizationRequestVerifierImpl(repository);
  }

  @Test
  public void verify() throws Exception {

    final ClientAuthorizationRequest authorizationRequest = new ClientAuthorizationRequest("type", clientId, "Code", "redirectURI");

    context.checking(new Expectations() {{
      oneOf(repository).findByCode(code);
      will(returnValue(Optional.of(authorizationRequest)));
    }});

    assertTrue(requestVerifier.verify(code, clientId));
  }

  @Test
  public void notVerifyRequestWhenNotExists() throws Exception {

    context.checking(new Expectations() {{
      oneOf(repository).findByCode(code);
      will(returnValue(Optional.absent()));
    }});

    assertFalse(requestVerifier.verify(code, clientId));
  }

  @Test
  public void notVerifiedWhenOtherClientIdWasPassed() throws Exception {

    final ClientAuthorizationRequest authorizationRequest = new ClientAuthorizationRequest("type", "other_clientId", "Code", "redirectURI");

    context.checking(new Expectations() {{
      oneOf(repository).findByCode(code);
      will(returnValue(Optional.of(authorizationRequest)));
    }});

    assertFalse(requestVerifier.verify(code, clientId));
  }
}