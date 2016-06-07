package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.Duration;
import com.clouway.oauth2.token.TokenRepositoryContractTest;
import com.clouway.oauth2.token.UrlSafeTokenGenerator;
import com.clouway.oauth2.token.Tokens;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class InMemoryTokensTest extends TokenRepositoryContractTest {

  @Override
  protected Tokens createRepo(Duration duration) {
    return new InMemoryTokens(new UrlSafeTokenGenerator(), duration);
  }
}