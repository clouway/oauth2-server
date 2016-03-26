package com.clouway.oauth2.token;

import com.google.common.base.Strings;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class RefreshTokenGeneratorImpl implements RefreshTokenGenerator {

  private TokenGenerator tokenGenerator;
  private Boolean generateNewRefreshToken;

  public RefreshTokenGeneratorImpl(TokenGenerator tokenGenerator, Boolean generateNewRefreshToken) {
    this.tokenGenerator = tokenGenerator;
    this.generateNewRefreshToken = generateNewRefreshToken;
  }

  @Override
  public String generate(String existingToken) {

    String refreshToken = existingToken;

    //if no refresh token or generate every time new refresh token
    if (Strings.isNullOrEmpty(refreshToken) || generateNewRefreshToken) {
      refreshToken = tokenGenerator.generate();
    }
    return refreshToken;
  }
}
