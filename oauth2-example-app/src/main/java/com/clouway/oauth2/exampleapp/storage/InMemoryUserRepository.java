package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.user.User;
import com.clouway.oauth2.user.UserIdFinder;
import com.clouway.oauth2.user.UserRepository;
import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
class InMemoryUserRepository implements UserIdFinder, UserRepository {

  @Override
  public Optional<String> find(Request request) {
    // get session id from cookie
    // and retrieve user information for that SID

    return Optional.of("testUserID");
  }

  @Override
  public Optional<User> load(String userId) {
    return Optional.of(new User("testUserID", "test@clouway.com", "Ivan Stefanov"));
  }
}
