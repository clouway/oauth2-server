package com.example.auth.core.user;

import com.google.common.base.Optional;
import com.google.inject.ImplementedBy;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@ImplementedBy(UserLoaderImpl.class)
public interface UserLoader {
  Optional<User> load(String token);
}
