package com.example.auth.core.token.refreshtoken;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public abstract class RefreshTokenRepositoryContractTest {

  protected RefreshTokenRepository repository;

  @Before
  public void setUp() throws Exception {
    repository = createRefreshTokenRepository();
  }

  @Test
  public void findById() throws Exception {

    RefreshToken refreshToken = new RefreshToken("value", "clientId", "secret");
    repository.save(refreshToken);


    Optional<RefreshToken> actualRefreshToken = repository.load(refreshToken.value);

    assertThat(actualRefreshToken.get(), is(refreshToken));
  }


  @Test
  public void delete() throws Exception {

    RefreshToken refreshToken = new RefreshToken("value", "clientId", "secret");
    repository.save(refreshToken);

    Optional<RefreshToken> actualRefreshToken = repository.load(refreshToken.value);
    assertThat(actualRefreshToken.get(), is(refreshToken));

    repository.delete(refreshToken.value);


    actualRefreshToken = repository.load(refreshToken.value);
    assertFalse(actualRefreshToken.isPresent());

  }

  @Test
  public void notExistingRefreshToken() throws Exception {
    Optional<RefreshToken> actualRefreshToken = repository.load("token");

    assertFalse(actualRefreshToken.isPresent());
  }

  protected abstract RefreshTokenRepository createRefreshTokenRepository();
}
