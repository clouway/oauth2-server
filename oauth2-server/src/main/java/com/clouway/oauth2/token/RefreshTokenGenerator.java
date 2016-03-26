package com.clouway.oauth2.token;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface RefreshTokenGenerator {

  String generate(String existingToken);
}
