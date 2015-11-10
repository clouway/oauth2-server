package com.clouway.oauth2.authorization;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

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
    Authorization clientAuthorization = new Authorization("type","clientId", authorizationCode, "redirectURI", "userId");
    repository.register(clientAuthorization);

    Optional<Authorization> actualClientAuthentication = repository.findByCode(authorizationCode);

    assertThat(actualClientAuthentication.get(), is(clientAuthorization));
  }

  @Test
  public void update() throws Exception {

    String authorizationCode = "code";
    Authorization clientAuthorization = new Authorization("type","clientId", authorizationCode, "redirectURI", "userId");
    repository.register(clientAuthorization);

    Optional<Authorization> actualClientAuthentication = repository.findByCode(authorizationCode);
    assertThat(actualClientAuthentication.get(), is(clientAuthorization));

    Authorization updatedAuthorization = new Authorization("type","clientId", authorizationCode, "redirectURI", "other user userId");
    updatedAuthorization.usedOn(new Date());
    repository.update(updatedAuthorization);

    actualClientAuthentication = repository.findByCode(authorizationCode);
    assertThat(actualClientAuthentication.get(), is(updatedAuthorization));
  }

  @Test
  public void notExistingCode() throws Exception {
    Optional<Authorization> client = repository.findByCode("id2");

    assertFalse(client.isPresent());
  }

  protected abstract ClientAuthorizationRepository createClientAuthorizationRepository();


}
