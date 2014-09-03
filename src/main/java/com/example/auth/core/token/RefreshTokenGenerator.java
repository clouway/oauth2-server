package com.example.auth.core.token;

import com.google.inject.ImplementedBy;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@ImplementedBy(RefreshTokenGeneratorImpl.class)
public interface RefreshTokenGenerator {

  String generate(String existingToken);
}
