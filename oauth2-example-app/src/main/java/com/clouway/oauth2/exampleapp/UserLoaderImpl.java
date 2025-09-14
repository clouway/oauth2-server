package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.token.BearerToken;
import com.clouway.oauth2.token.Tokens;
import com.clouway.oauth2.token.User;
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

    Optional<BearerToken> token = tokens.findTokenAvailableAt(tokenValue, new DateTime());
    if (!token.isPresent()) {
      return Optional.absent();
    }

    String subjectId;
    if (token.get().subject instanceof com.clouway.oauth2.token.Subject.User) {
      subjectId = ((com.clouway.oauth2.token.Subject.User) token.get().subject).getId();
    } else if (token.get().subject instanceof com.clouway.oauth2.token.Subject.ServiceAccount) {
      subjectId = ((com.clouway.oauth2.token.Subject.ServiceAccount) token.get().subject).clientEmail();
    } else {
      return Optional.absent();
    }
    Optional<User> user = repository.load(subjectId);
    if (!token.isPresent()) {
      return Optional.absent();
    }

    return user;
  }
}
