package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.TokenRepository;
import com.clouway.oauth2.user.User;
import com.clouway.oauth2.user.UserRepository;
import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class UserLoaderImpl implements UserLoader {

  private UserRepository repository;
  private TokenRepository tokenRepository;

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
