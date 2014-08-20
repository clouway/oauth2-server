package com.example.auth.core.token;

import com.example.auth.core.Duration;
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

  private Duration timeToLive = new Duration(900000000l);

  protected abstract TokenRepository createRepo(Date currentDate, Duration timeToLive);


  @Test
  public void happyPath() throws Exception {
    final Date expirationTime = new Date(System.currentTimeMillis() + 9000000);
    final Token notExpiredToken = new Token("9c5084d190264d0de737a8049ed630fd", "bearer", expirationTime);

    repository = createRepo(new Date(), timeToLive);

    repository.save(notExpiredToken);

    Optional<Token> tokenOptional = repository.getNotExpiredToken(notExpiredToken.value);

    assertThat(tokenOptional.get().value, is(equalTo(notExpiredToken.value)));
    assertThat(tokenOptional.get().type, is(equalTo(notExpiredToken.type)));
    assertThat(tokenOptional.get().expiration, is(equalTo(new Date(expirationTime.getTime() + timeToLive.milliseconds))));
  }

  @Test
  public void notExistingToken() throws Exception {
    repository = createRepo(new Date(), timeToLive);
    Optional<Token> tokenOptional = repository.getNotExpiredToken("token.value");

    assertFalse(tokenOptional.isPresent());
  }

  @Test
  public void expiredToken() throws Exception {
    final Date expirationTime = new Date(System.currentTimeMillis());
    final Token token = new Token("9c5084d190264d0de737a8049ed630fd", "bearer", expirationTime);


    Date currentDate = new Date(System.currentTimeMillis() + 9000000);
    repository = createRepo(currentDate, timeToLive);

    repository.save(token);

    Optional<Token> tokenOptional = repository.getNotExpiredToken(token.value);

    assertFalse(tokenOptional.isPresent());
  }

}

