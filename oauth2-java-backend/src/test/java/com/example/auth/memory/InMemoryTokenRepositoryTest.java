package com.example.auth.memory;

import com.example.auth.core.token.TokenRepository;
import com.example.auth.core.token.TokenRepositoryContractTest;

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