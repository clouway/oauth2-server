package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.token.Token;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.user.User;
import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class UserLoaderImpl implements UserLoader {

  private UserRepository repository;
  private Tokens tokens;

  public UserLoaderImpl(UserRepository repository, Tokens tokens) {
    this.repository = repository;
    this.tokens = tokens;
  }

  @Override
  public Optional<User> load(String tokenValue) {

    Optional<Token> token = tokens.getNotExpiredToken(tokenValue);
    if (!token.isPresent()) {
      return Optional.absent();
    }

    Optional<User> user = repository.load(token.get().identityId);
    if (!token.isPresent()) {
      return Optional.absent();
    }

    return user;
  }
}
