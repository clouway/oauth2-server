package com.example.auth.core.user;

import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface UserRepository {
  Optional<User> load(String userId);
}
