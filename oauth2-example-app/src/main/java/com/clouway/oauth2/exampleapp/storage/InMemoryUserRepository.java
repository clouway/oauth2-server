package com.clouway.oauth2.exampleapp.storage;

import com.clouway.friendlyserve.Request;
import com.clouway.oauth2.ResourceOwnerIdentityFinder;
import com.clouway.oauth2.common.DateTime;
import com.clouway.oauth2.exampleapp.UserRepository;
import com.clouway.oauth2.token.FindIdentityRequest;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.Identity;
import com.clouway.oauth2.token.IdentityFinder;
import com.clouway.oauth2.token.User;
import com.google.common.base.Optional;

import java.util.Collections;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
class InMemoryUserRepository implements IdentityFinder, ResourceOwnerIdentityFinder, UserRepository {

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
  public Optional<Identity> findIdentity(FindIdentityRequest request) {
    if (request.grantType == GrantType.AUTHORIZATION_CODE) {
      return Optional.of(new Identity("testUserID", "testUser", "test User", "User Family", "test@clouway.com", null, Collections.<String, Object>emptyMap()));
    } else {
      return Optional.of(new Identity("customerapp@testapp.com", "testapp@testapp.com", "Customer Portal", "", "", "", Collections.<String, Object>emptyMap()));
    }
  }

  @Override
  public Optional<User> load(String userId) {
    return Optional.of(new User("testUserID", "test@clouway.com", "Ivan Stefanov"));
  }
}
