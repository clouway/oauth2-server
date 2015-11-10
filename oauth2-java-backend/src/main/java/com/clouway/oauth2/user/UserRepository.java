package com.clouway.oauth2.user;

import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface UserRepository {
  Optional<User> load(String userId);
}
