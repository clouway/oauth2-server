package com.clouway.oauth2.token;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class RefreshTokenGeneratorImpl implements RefreshTokenGenerator {

  private TokenGenerator tokenGenerator;
  private Provider<Boolean> generateNewRefreshToken;

  @Inject
  public RefreshTokenGeneratorImpl(TokenGenerator tokenGenerator, Provider<Boolean> generateNewRefreshToken) {
    this.tokenGenerator = tokenGenerator;
    this.generateNewRefreshToken = generateNewRefreshToken;
  }

  @Override
  public String generate(String existingToken) {

    String refreshToken = existingToken;

    //if no refresh token or generate every time new refresh token
    if (Strings.isNullOrEmpty(refreshToken) || generateNewRefreshToken.get()) {
      refreshToken = tokenGenerator.generate();
    }
    return refreshToken;
  }
}
