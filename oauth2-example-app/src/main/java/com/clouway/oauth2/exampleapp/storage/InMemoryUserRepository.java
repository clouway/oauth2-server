package com.clouway.oauth2.exampleapp.storage;

import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.exampleapp.UserRepository;
import com.clouway.oauth2.http.Request;
import com.clouway.oauth2.user.IdentityFinder;
import com.clouway.oauth2.user.User;
import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
class InMemoryUserRepository implements IdentityFinder, UserRepository {

  @Override
  public Optional<String> find(Request request, DateTime instantTime) {
    // get session id from cookie
    // and retrieve user information for that SID
    for (String sid : request.cookie("SID")) {
      return Optional.of("testUserID");
    }

    return Optional.absent();
  }

  @Override
  public Optional<User> load(String userId) {
    return Optional.of(new User("testUserID", "test@clouway.com", "Ivan Stefanov"));
  }
}
