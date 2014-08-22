package com.example.auth.core.token;

import com.google.inject.ImplementedBy;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@ImplementedBy(BearerTokenCreator.class)
public interface TokenCreator {
  Token create();
}
