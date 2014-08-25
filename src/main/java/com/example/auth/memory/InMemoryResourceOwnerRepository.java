package com.example.auth.memory;

import com.example.auth.core.ResourceOwner;
import com.example.auth.core.ResourceOwnerAuthentication;
import com.example.auth.core.ResourceOwnerStore;
import com.example.auth.core.session.Session;
import com.example.auth.core.session.SessionSecurity;
import com.example.auth.core.token.TokenGenerator;
import com.google.common.base.Optional;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryResourceOwnerRepository implements ResourceOwnerStore, ResourceOwnerAuthentication, SessionSecurity {
  private Map<String, ResourceOwner> resourceOwners = new HashMap<String, ResourceOwner>() {{
    put("admin", new ResourceOwner("admin", "admin"));
  }};

  private Set<Session> sessions = new HashSet<Session>();

  private TokenGenerator tokenGenerator;

  @Inject
  InMemoryResourceOwnerRepository(TokenGenerator tokenGenerator) {
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public void save(ResourceOwner resourceOwner) {
    resourceOwners.put(resourceOwner.username, resourceOwner);
  }

  @Override
  public Optional<Session> auth(String username, String password, String remoteAddress) {
    if (resourceOwners.containsKey(username)) {
      if (password.equals(resourceOwners.get(username).password)) {
        Session session = new Session(tokenGenerator.generate());

        sessions.add(session);

        return Optional.of(session);
      }
    }

    return Optional.absent();
  }

  @Override
  public Boolean exists(Session session) {
    return sessions.contains(session);
  }
}