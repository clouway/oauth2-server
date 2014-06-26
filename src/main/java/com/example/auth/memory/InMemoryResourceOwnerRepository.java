package com.example.auth.memory;

import com.example.auth.core.ResourceOwner;
import com.example.auth.core.ResourceOwnerRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class InMemoryResourceOwnerRepository implements ResourceOwnerRepository {
  private Map<String, ResourceOwner> resourceOwners = new HashMap<String, ResourceOwner>() {{
    put("FeNoMeNa", new ResourceOwner("FeNoMeNa", "parola"));
  }};

  @Override
  public void save(ResourceOwner resourceOwner) {
    resourceOwners.put(resourceOwner.username, resourceOwner);
  }

  @Override
  public Boolean exist(String username, String password) {
    if (resourceOwners.containsKey(username)) {
      return password.equals(resourceOwners.get(username).password);
    }

    return false;
  }
}