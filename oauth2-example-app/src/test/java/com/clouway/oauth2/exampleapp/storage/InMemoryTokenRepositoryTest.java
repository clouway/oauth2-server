package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.Duration;
import com.clouway.oauth2.token.TokenRepositoryContractTest;
import com.clouway.oauth2.token.Sha1TokenGenerator;
import com.clouway.oauth2.token.TokenRepository;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryTokenRepositoryTest extends TokenRepositoryContractTest {

  @Override
  protected TokenRepository createRepo(Date currentDate, Duration duration) {
    return new InMemoryTokenRepository(new Sha1TokenGenerator(), currentDate, duration);
  }
}