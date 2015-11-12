package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.token.TokenRepository;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryTokenRepositoryTest extends TokenRepositoryContractTest {

  @Override
  protected TokenRepository createRepo(Date currentDate) {
    return new InMemoryTokenRepository(currentDate, timeToLive);
  }
}