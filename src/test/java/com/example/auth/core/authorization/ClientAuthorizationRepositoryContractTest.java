package com.example.auth.core.authorization;

import com.example.auth.core.ClientAuthorizationRequest;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public abstract class ClientAuthorizationRepositoryContractTest {

  protected ClientAuthorizationRepository repository;

  @Before
  public void setUp() throws Exception {
    repository = createClientAuthorizationRepository();
  }

  @Test
  public void findByCode() throws Exception {

    String authorizationCode = "code";
    ClientAuthorizationRequest clientClientAuthorizationRequest = new ClientAuthorizationRequest("type","clientId", authorizationCode, "redirectURI");
    repository.register(clientClientAuthorizationRequest);

    Optional<ClientAuthorizationRequest> actualClientAuthentication = repository.findByCode(authorizationCode);

    assertThat(actualClientAuthentication.get(), is(clientClientAuthorizationRequest));
  }

  @Test
  public void notExistingCode() throws Exception {
    Optional<ClientAuthorizationRequest> client = repository.findByCode("id2");

    assertFalse(client.isPresent());
  }

  protected abstract ClientAuthorizationRepository createClientAuthorizationRepository();


}
