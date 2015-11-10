package com.example.auth.memory;

import com.clouway.oauth2.user.User;
import com.clouway.oauth2.user.UserIdFinder;
import com.clouway.oauth2.user.UserRepository;
import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
class InMemoryUserRepository implements UserIdFinder, UserRepository {
  @Override
  public String find(String sessionId) {
    return "testUserID";
  }

  @Override
  public Optional<User> load(String userId) {
    return Optional.of(new User("testUserID","test@clouway.com", "Ivan Stefanov"));
  }
}
