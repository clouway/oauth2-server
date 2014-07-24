package com.example.auth.core.token;

import com.google.inject.ImplementedBy;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@ImplementedBy(TokenFactoryImpl.class)
public interface TokenFactory {
  Token create();
}
