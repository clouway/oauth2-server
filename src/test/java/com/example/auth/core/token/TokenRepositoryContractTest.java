package com.example.auth.core.token;

import com.example.auth.core.Duration;
import com.google.common.base.Optional;
import org.junit.Test;

import java.util.Date;

import static com.example.auth.core.Duration.hours;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public abstract class TokenRepositoryContractTest {

  private TokenRepository repository;

  protected Duration timeToLive = hours(1);//
  private final Date currentDate = new Date();

  protected abstract TokenRepository createRepo(Date currentDate);


  @Test
  public void happyPath() throws Exception {
    final Token notExpiredToken = new Token("9c5084d190264d0de737a8049ed630fd", "bearer", "userId", timeToLive.seconds, currentDate);

    repository = createRepo(new Date());

    repository.save(notExpiredToken);

    Optional<Token> tokenOptional = repository.getNotExpiredToken(notExpiredToken.value);

    assertThat(tokenOptional.get().value, is(equalTo(notExpiredToken.value)));
    assertThat(tokenOptional.get().type, is(equalTo(notExpiredToken.type)));
    assertThat(tokenOptional.get().creationDate, is(equalTo(currentDate)));
    assertThat(tokenOptional.get().expiresInSeconds, is(equalTo(3600l)));
  }

  @Test
  public void notExistingToken() throws Exception {
    repository = createRepo(new Date());
    Optional<Token> tokenOptional = repository.getNotExpiredToken("token.value");

    assertFalse(tokenOptional.isPresent());
  }

  @Test
  public void expiredToken() throws Exception {
    //created two hours ago
    final Date creationDate = new Date(System.currentTimeMillis() - hours(2).asMills());
    final Token token = new Token("9c5084d190264d0de737a8049ed630fd", "bearer", "userId", timeToLive.seconds, creationDate);


    Date currentDate = new Date(System.currentTimeMillis() + 9000000);
    repository = createRepo(currentDate);

    repository.save(token);

    Optional<Token> tokenOptional = repository.getNotExpiredToken(token.value);

    assertFalse(tokenOptional.isPresent());
  }

}

