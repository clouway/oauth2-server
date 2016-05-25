package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.user.User;
import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface UserRepository {
  Optional<User> load(String userId);
}
