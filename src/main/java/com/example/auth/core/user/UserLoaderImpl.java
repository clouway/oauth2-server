package com.example.auth.core.user;

import com.example.auth.core.token.Token;
import com.example.auth.core.token.TokenRepository;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */

class UserLoaderImpl implements UserLoader {

  private UserRepository repository;
  private TokenRepository tokenRepository;

  @Inject
  public UserLoaderImpl(UserRepository repository, TokenRepository tokenRepository) {
    this.repository = repository;
    this.tokenRepository = tokenRepository;
  }

  @Override
  public Optional<User> load(String tokenValue) {

    Optional<Token> token = tokenRepository.getNotExpiredToken(tokenValue);
    if (!token.isPresent()) {
      return Optional.absent();
    }

    Optional<User> user = repository.load(token.get().userId);
    if (!token.isPresent()) {
      return Optional.absent();
    }

    return user;
  }
}
