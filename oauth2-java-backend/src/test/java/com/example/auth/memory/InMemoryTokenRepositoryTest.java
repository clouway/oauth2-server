package com.example.auth.memory;

import com.clouway.oauth2.token.TokenRepository;
import com.clouway.oauth2.token.TokenRepositoryContractTest;

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