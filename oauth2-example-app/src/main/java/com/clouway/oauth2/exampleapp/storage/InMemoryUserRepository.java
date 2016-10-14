package com.clouway.oauth2.exampleapp.storage;

import com.clouway.friendlyserve.Request;
import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.Identity;
import com.clouway.oauth2.exampleapp.UserRepository;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.user.IdentityFinder;
import com.clouway.oauth2.user.ResourceOwnerIdentityFinder;
import com.clouway.oauth2.user.User;
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
  public Optional<Identity> findIdentity(String identityId, GrantType grantType, DateTime instantTime) {
    if (grantType == GrantType.AUTHORIZATION_CODE) {
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
