package com.example.auth.memory;

import com.example.auth.core.user.User;
import com.example.auth.core.user.UserIdFinder;
import com.example.auth.core.user.UserRepository;
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
