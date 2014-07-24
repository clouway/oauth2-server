package com.example.auth.core;

import com.example.auth.core.token.Token;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface AccessTokenGenerator {
  Token generate();
}