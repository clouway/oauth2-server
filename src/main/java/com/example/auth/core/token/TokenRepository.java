package com.example.auth.core.token;

import com.google.common.base.Optional;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface TokenRepository {

  void save(Token token);

  Optional<Token> getNotExpiredToken(String token);
}