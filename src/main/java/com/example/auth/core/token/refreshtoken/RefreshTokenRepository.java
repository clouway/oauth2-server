package com.example.auth.core.token.refreshtoken;

import com.google.common.base.Optional;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface RefreshTokenRepository {
  Optional<RefreshToken> load(String id);

  void delete(String id);

  void save(RefreshToken refreshToken);
}
