package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.token.User;
import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface UserLoader {
  Optional<User> load(String token);
}
