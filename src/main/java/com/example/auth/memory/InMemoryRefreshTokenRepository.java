package com.example.auth.memory;

import com.example.auth.core.token.refreshtoken.RefreshToken;
import com.example.auth.core.token.refreshtoken.RefreshTokenRepository;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

  private final Map<String, RefreshToken> tokens = Maps.newHashMap();

  @Override
  public Optional<RefreshToken> load(String id) {
    if(tokens.containsKey(id)){

      return Optional.of(tokens.get(id));
    }
    return Optional.absent();
  }

  @Override
  public void delete(String id) {
    if (tokens.containsKey(id)) {
      tokens.remove(id);
    }
  }

  @Override
  public void save(RefreshToken refreshToken) {
    tokens.put(refreshToken.value, refreshToken);
  }
}
