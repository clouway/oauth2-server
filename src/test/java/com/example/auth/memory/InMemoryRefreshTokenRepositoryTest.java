package com.example.auth.memory;

import com.example.auth.core.token.refreshtoken.RefreshTokenRepository;
import com.example.auth.core.token.refreshtoken.RefreshTokenRepositoryContractTest;

public class InMemoryRefreshTokenRepositoryTest  extends RefreshTokenRepositoryContractTest {

  @Override
  protected RefreshTokenRepository createRefreshTokenRepository() {
    return new InMemoryRefreshTokenRepository();
  }

}