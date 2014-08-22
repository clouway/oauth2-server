package com.example.auth.core.token;


/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface TokenSecurity {

  void validateRefreshToken(TokenRequest tokenRequest);

  void validateAuthCode(TokenRequest tokenRequest);
}