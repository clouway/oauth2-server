package com.example.auth.core.token;

import com.google.inject.ImplementedBy;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface TokenFactory {
  Token create();
}
