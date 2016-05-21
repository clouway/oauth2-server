package com.clouway.oauth2.token;

import com.clouway.oauth2.Duration;
import com.google.common.base.Optional;
import org.junit.Test;

import java.util.Date;

import static com.clouway.oauth2.Duration.hours;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public abstract class TokenRepositoryContractTest {

  private static final Duration oneHour = hours(1);

  private TokenRepository repository;
  private final Date currentDate = new Date();

  @Test
  public void happyPath() throws Exception {
    repository = createRepo(currentDate, oneHour);

    Token issuedToken = repository.issueToken("::user1::", Optional.<String>absent());

    Optional<Token> tokenOptional = repository.getNotExpiredToken(issuedToken.value);

    assertThat(tokenOptional.get().value, is(equalTo(issuedToken.value)));
    assertThat(tokenOptional.get().type, is(equalTo(issuedToken.type)));
    assertThat(tokenOptional.get().refreshToken, is(equalTo(issuedToken.refreshToken)));
    assertThat(tokenOptional.get().creationDate, is(equalTo(currentDate)));
    assertThat(tokenOptional.get().expiresInSeconds, is(equalTo(3600L)));
  }

  @Test
  public void refreshToken() throws Exception {
    repository = createRepo(currentDate, oneHour);

    Token newlyIssuedToken = repository.issueToken("identityId", Optional.<String>absent());

    Optional<Token> tokenOptional = repository.refreshToken(newlyIssuedToken.refreshToken);

    assertThat(tokenOptional.get().value, is(equalTo(newlyIssuedToken.value)));
    assertThat(tokenOptional.get().type, is(equalTo(newlyIssuedToken.type)));
    assertThat(tokenOptional.get().refreshToken, is(equalTo(newlyIssuedToken.refreshToken)));
    assertThat(tokenOptional.get().creationDate, is(equalTo(currentDate)));
    assertThat(tokenOptional.get().expiresInSeconds, is(equalTo(oneHour.seconds)));
  }

  @Test
  public void tryToRefreshNonExistingToken() throws Exception {
    repository = createRepo(new Date(), oneHour);
    Optional<Token> tokenOptional = repository.refreshToken("refreshToken.value");

    assertFalse(tokenOptional.isPresent());
  }

  @Test
  public void expiredToken() throws Exception {
    //created two hours ago
    final Date creationDate = new Date(System.currentTimeMillis() - hours(2).asMills());
    final Token token = new Token("9c5084d190264d0de737a8049ed630fd", "bearer", "refresh", "identityId", oneHour.seconds, creationDate);

    Date currentDate = new Date(System.currentTimeMillis() + 9000000);
    repository = createRepo(currentDate, oneHour);

    repository.issueToken("identityId", Optional.<String>absent());

    Optional<Token> tokenOptional = repository.getNotExpiredToken(token.value);

    assertFalse(tokenOptional.isPresent());
  }

  @Test
  public void refreshExpiredToken() throws Exception {
    //created two hours ago
    Date currentDate = new Date(System.currentTimeMillis() + 9000000);

    repository = createRepo(currentDate, oneHour);
    Token token = repository.issueToken("::user2::", Optional.<String>absent());

    Optional<Token> tokenOptional = repository.refreshToken(token.refreshToken);

    assertThat(tokenOptional.get().value, is(equalTo(token.value)));
    assertThat(tokenOptional.get().type, is(equalTo(token.type)));
    assertThat(tokenOptional.get().refreshToken, is(equalTo(token.refreshToken)));
  }

  protected abstract TokenRepository createRepo(Date currentDate, Duration duration);

}

