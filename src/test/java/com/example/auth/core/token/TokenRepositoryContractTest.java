package com.example.auth.core.token;

import com.google.common.base.Optional;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public abstract class TokenRepositoryContractTest {

  private TokenRepository repository;

  protected abstract TokenRepository createRepo(Date currentDate);


  @Test
  public void happyPath() throws Exception {
    final Date expirationTime = new Date(System.currentTimeMillis() + 9000000);
    final Token token = new Token("9c5084d190264d0de737a8049ed630fd", "bearer", expirationTime);

    repository = createRepo(new Date());

    repository.save(token);

    Optional<Token> tokenOptional = repository.getNotExpiredToken(token.value);

    assertThat(tokenOptional.get().value, is(equalTo(token.value)));
    assertThat(tokenOptional.get().type, is(equalTo(token.type)));
    assertThat(tokenOptional.get().expiration, is(equalTo(token.expiration)));
  }

  @Test
  public void notExistingToken() throws Exception {
    repository = createRepo(new Date());
    Optional<Token> tokenOptional = repository.getNotExpiredToken("token.value");

    assertFalse(tokenOptional.isPresent());
  }

  @Test
  public void expiredToken() throws Exception {
    final Date expirationTime = new Date(System.currentTimeMillis());
    final Token token = new Token("9c5084d190264d0de737a8049ed630fd", "bearer", expirationTime);


    Date currentDate = new Date(System.currentTimeMillis() + 9000000);
    repository = createRepo(currentDate);

    repository.save(token);

    Optional<Token> tokenOptional = repository.getNotExpiredToken(token.value);

    assertFalse(tokenOptional.isPresent());
  }

}

